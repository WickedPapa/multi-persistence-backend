package it.montano.sqlvsnosql.service;

import it.montano.sqlvsnosql.dto.OrderRequest;
import it.montano.sqlvsnosql.dto.OrderResponse;
import it.montano.sqlvsnosql.entity.OrderEntity;
import it.montano.sqlvsnosql.mapper.OrderMapper;
import it.montano.sqlvsnosql.repository.OrderRepository;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository repository;
  private final OrderMapper orderMapper;

  public @NonNull OrderResponse createOrder(@NonNull OrderRequest request) {
    OrderEntity order = orderMapper.toEntity(request);
    OrderEntity saved = repository.save(order);
    return orderMapper.toResponse(saved);
  }

  public @NonNull List<OrderResponse> getOrders() {
    return repository.findAll().stream().map(orderMapper::toResponse).toList();
  }
}
