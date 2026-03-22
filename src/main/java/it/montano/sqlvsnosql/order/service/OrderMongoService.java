package it.montano.sqlvsnosql.order.service;

import it.montano.sqlvsnosql.common.dto.OrderItemRequestDto;
import it.montano.sqlvsnosql.common.dto.OrderRequestDto;
import it.montano.sqlvsnosql.common.exeption.ResourceNotFoundException;
import it.montano.sqlvsnosql.common.mapper.OrderMapper;
import it.montano.sqlvsnosql.dto.*;
import it.montano.sqlvsnosql.order.model.OrderDocument;
import it.montano.sqlvsnosql.order.repository.OrderMongoRepository;
import it.montano.sqlvsnosql.product.service.ProductService;
import it.montano.sqlvsnosql.user.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "MONGODB")
public class OrderMongoService implements OrderService {

  private final UserService userService;
  private final ProductService productService;
  private final OrderMongoRepository repo;
  private final OrderMapper mapper;

  @Override
  public @NonNull OrderResponse createOrder(@NonNull OrderRequest request) {
    OrderRequestDto orderItemRequestDto = mapper.toDto(request);
    enrichOrderItems(orderItemRequestDto);
    OrderDocument saved =
        repo.save(
            mapper.toDocument(
                orderItemRequestDto, userService.getUserById(orderItemRequestDto.getUserId())));
    return mapper.toResponse(saved);
  }

  @Override
  public void deleteOrder(@NonNull UUID orderId) {
    repo.deleteById(orderId);
  }

  @Override
  public @NonNull List<MostSoldProductResponse> getMostSoldProducts() {
    return repo.getMostSoldProduct();
  }

  @Override
  public @NonNull OrderResponse getOrderById(@NonNull UUID orderId) {
    return repo.findById(orderId).map(mapper::toResponse).orElseThrow(() ->  new ResourceNotFoundException(orderId.toString()));
  }

  @Override
  public @NonNull List<OrderResponse> getOrdersByUserId(@NonNull UUID userId) {
    return repo.findByUserUserId(userId).stream().map(mapper::toResponse).toList();
  }

  @Override
  public @NonNull List<OrderResponse> getOrders() {
    return repo.findAll().stream().map(mapper::toResponse).toList();
  }

  @Override
  public @NonNull List<TotalSpentPerUserResponse> getTotalSpentPerUser() {
    return repo.getTotalSpentPerUser();
  }

  private void enrichOrderItems(@NonNull OrderRequestDto orderRequestDto) {
    orderRequestDto.getItems().forEach(this::fillItemPrice);
  }

  private void fillItemPrice(@NonNull OrderItemRequestDto orderItemRequestDto) {
    ProductResponse productResponse =
        productService.getProductById(orderItemRequestDto.getProductId());
    orderItemRequestDto.setPrice(productResponse.getPrice());
    orderItemRequestDto.setName(productResponse.getName());
  }
}
