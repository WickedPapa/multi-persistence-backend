package it.montano.sqlvsnosql.order.service;

import it.montano.sqlvsnosql.common.dto.OrderItemRequestDto;
import it.montano.sqlvsnosql.common.dto.OrderRequestDto;
import it.montano.sqlvsnosql.common.mapper.OrderMapper;
import it.montano.sqlvsnosql.dto.OrderItemResponse;
import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import it.montano.sqlvsnosql.order.model.OrderEntity;
import it.montano.sqlvsnosql.order.repository.OrderPostgresRepository;
import it.montano.sqlvsnosql.product.service.ProductService;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "datasource", havingValue = "POSTGRES")
public class OrderPostgresService implements OrderService {

  private final ProductService productService;
  private final OrderPostgresRepository repo;
  private final OrderMapper mapper;

  @Override
  public @NonNull OrderResponse createOrder(@NonNull OrderRequest request) {
    OrderRequestDto orderItemRequestDto = mapper.toDto(request);
    enrichOrderItems(orderItemRequestDto);
    OrderEntity saved = repo.save(mapper.toEntity(orderItemRequestDto));
    return mapper.toResponse(saved);
  }

  @Override
  public void deleteOrder(@NonNull UUID orderId) {
    repo.deleteById(orderId);
  }

  @Override
  public @NonNull OrderResponse getOrderById(@NonNull UUID orderId) {
    return repo.findById(orderId).map(mapper::toResponse).map(this::enrichOrderItems).orElseThrow();
  }

  @Override
  public @NonNull List<OrderResponse> getOrdersByUserId(@NonNull UUID userId) {
    return repo.findByUserId(userId).stream()
        .map(mapper::toResponse)
        .map(this::enrichOrderItems)
        .toList();
  }

  @Override
  public @NonNull List<OrderResponse> getOrders() {
    return repo.findAll().stream().map(mapper::toResponse).map(this::enrichOrderItems).toList();
  }

  private void enrichOrderItems(@NonNull OrderRequestDto orderRequestDto) {
    orderRequestDto.getItems().forEach(this::fillItemPrice);
  }

  private OrderResponse enrichOrderItems(@NonNull OrderResponse orderResponse) {
    orderResponse.getItems().forEach(this::fillItemPrice);
    return orderResponse;
  }

  private void fillItemPrice(@NonNull OrderItemRequestDto orderItemRequestDto) {
    orderItemRequestDto.setUnitPrice(
        productService.getProductById(orderItemRequestDto.getProductId()).getPrice());
  }

  private void fillItemPrice(@NonNull OrderItemResponse orderItemResponse) {
    orderItemResponse.setName(
        productService.getProductById(orderItemResponse.getProductId()).getName());
  }
}
