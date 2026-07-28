package it.montano.multipersistencebackend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.montano.multipersistencebackend.common.constant.Datasources;
import it.montano.multipersistencebackend.dto.MostSoldProductResponse;
import it.montano.multipersistencebackend.dto.OrderItemRequest;
import it.montano.multipersistencebackend.dto.OrderRequest;
import it.montano.multipersistencebackend.dto.ProductRequest;
import it.montano.multipersistencebackend.dto.ProductResponse;
import it.montano.multipersistencebackend.dto.TotalSpentPerUserResponse;
import it.montano.multipersistencebackend.dto.UserRequest;
import it.montano.multipersistencebackend.dto.UserResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {AbstractMongoIntegrationTest.PROPERTY_KEY_EQUALS + Datasources.MONGO})
@AutoConfigureMockMvc
class MongoOrderStatsE2ETest extends AbstractMongoIntegrationTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @Test
  void shouldExposeExpectedOrderStatsFromApi() throws Exception {
    String suffix = UUID.randomUUID().toString().substring(0, 8);

    UserResponse firstUser =
        createUser(
            Instancio.of(UserRequest.class)
                .set(field(UserRequest::getFirstName), "Alice")
                .set(field(UserRequest::getLastName), "Benchmark")
                .set(field(UserRequest::getEmail), "alice-" + suffix + "@example.test")
                .create());
    UserResponse secondUser =
        createUser(
            Instancio.of(UserRequest.class)
                .set(field(UserRequest::getFirstName), "Bob")
                .set(field(UserRequest::getLastName), "Benchmark")
                .set(field(UserRequest::getEmail), "bob-" + suffix + "@example.test")
                .create());

    ProductResponse firstProduct =
        createProduct(
            Instancio.of(ProductRequest.class)
                .set(field(ProductRequest::getName), "cpu-" + suffix)
                .set(field(ProductRequest::getPrice), new BigDecimal("10.00"))
                .create());
    ProductResponse secondProduct =
        createProduct(
            Instancio.of(ProductRequest.class)
                .set(field(ProductRequest::getName), "ram-" + suffix)
                .set(field(ProductRequest::getPrice), new BigDecimal("5.00"))
                .create());

    createOrder(
        firstUser.getId(),
        List.of(
            Instancio.of(OrderItemRequest.class)
                .set(field(OrderItemRequest::getProductId), firstProduct.getId())
                .set(field(OrderItemRequest::getQuantity), 2)
                .create(),
            Instancio.of(OrderItemRequest.class)
                .set(field(OrderItemRequest::getProductId), secondProduct.getId())
                .set(field(OrderItemRequest::getQuantity), 1)
                .create()));
    createOrder(
        firstUser.getId(),
        List.of(
            Instancio.of(OrderItemRequest.class)
                .set(field(OrderItemRequest::getProductId), firstProduct.getId())
                .set(field(OrderItemRequest::getQuantity), 1)
                .create()));
    createOrder(
        secondUser.getId(),
        List.of(
            Instancio.of(OrderItemRequest.class)
                .set(field(OrderItemRequest::getProductId), secondProduct.getId())
                .set(field(OrderItemRequest::getQuantity), 4)
                .create()));

    List<MostSoldProductResponse> mostSoldProducts =
        objectMapper.readValue(
            mockMvc
                .perform(get("/orders/stats/most-sold-products"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    List<TotalSpentPerUserResponse> totalsPerUser =
        objectMapper.readValue(
            mockMvc
                .perform(get("/orders/stats/total-spent-per-user"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    Map<String, MostSoldProductResponse> mostSoldByName =
        mostSoldProducts.stream()
            .collect(Collectors.toMap(MostSoldProductResponse::getName, Function.identity()));

    assertThat(mostSoldProducts).hasSize(2);
    assertThat(mostSoldProducts.get(0).getTotalQuantity())
        .isGreaterThanOrEqualTo(mostSoldProducts.get(1).getTotalQuantity());
    assertThat(mostSoldByName.get(firstProduct.getName()).getProductId())
        .isEqualTo(firstProduct.getId());
    assertThat(mostSoldByName.get(firstProduct.getName()).getTotalQuantity()).isEqualTo(3L);
    assertThat(mostSoldByName.get(secondProduct.getName()).getProductId())
        .isEqualTo(secondProduct.getId());
    assertThat(mostSoldByName.get(secondProduct.getName()).getTotalQuantity()).isEqualTo(5L);

    Map<UUID, TotalSpentPerUserResponse> totalsByUserId =
        totalsPerUser.stream()
            .collect(Collectors.toMap(TotalSpentPerUserResponse::getUserId, Function.identity()));

    assertThat(totalsPerUser).hasSize(2);
    assertThat(totalsByUserId.get(firstUser.getId()).getFirstName()).isEqualTo("Alice");
    assertThat(totalsByUserId.get(firstUser.getId()).getTotalSpent()).isEqualByComparingTo("35.00");
    assertThat(totalsByUserId.get(secondUser.getId()).getFirstName()).isEqualTo("Bob");
    assertThat(totalsByUserId.get(secondUser.getId()).getTotalSpent())
        .isEqualByComparingTo("20.00");
  }

  private UserResponse createUser(UserRequest request) throws Exception {
    return objectMapper.readValue(
        mockMvc
            .perform(
                post("/users")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString(),
        UserResponse.class);
  }

  private ProductResponse createProduct(ProductRequest request) throws Exception {
    return objectMapper.readValue(
        mockMvc
            .perform(
                post("/products")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString(),
        ProductResponse.class);
  }

  private void createOrder(UUID userId, List<OrderItemRequest> items) throws Exception {
    OrderRequest request =
        Instancio.of(OrderRequest.class)
            .set(field(OrderRequest::getUserId), userId)
            .set(field(OrderRequest::getItems), items)
            .create();
    mockMvc
        .perform(
            post("/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }
}
