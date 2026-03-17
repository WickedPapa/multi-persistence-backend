package it.montano.sqlvsnosql.service;

import it.montano.sqlvsnosql.dto.OrderItemResponse;
import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import it.montano.sqlvsnosql.entity.OrderEntity;
import it.montano.sqlvsnosql.entity.OrderItemEntity;
import it.montano.sqlvsnosql.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository repository;

  public OrderResponse createOrder(OrderRequest request) {

    OrderEntity order = new OrderEntity();
    order.setUserId(request.getUserId());

    List<OrderItemEntity> items =
        request.getItems().stream()
            .map(
                i -> {
                  OrderItemEntity item = new OrderItemEntity();
                  item.setProductId(i.getProductId());
                  item.setQuantity(i.getQuantity());
                  item.setOrder(order);
                  return item;
                })
            .toList();

    order.setItems(items);

    OrderEntity saved = repository.save(order);

    return mapToResponse(saved);
  }

  public List<OrderResponse> getOrders() {
    return repository.findAll().stream().map(this::mapToResponse).toList();
  }

  private OrderResponse mapToResponse(OrderEntity entity) {

    List<OrderItemResponse> items =
        entity.getItems().stream()
            .map(
                i -> {
                  OrderItemResponse r = new OrderItemResponse();
                  r.setProductId(i.getProductId());
                  r.setQuantity(i.getQuantity());
                  return r;
                })
            .toList();

    OrderResponse response = new OrderResponse();
    response.setId(entity.getId());
    response.setUserId(entity.getUserId());
    response.setItems(items);

    return response;
  }
}
