package it.montano.sqlvsnosql.order.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  UUID productId;
  Integer quantity;
  Double unitPrice;

  @ManyToOne
  @JoinColumn(name = "order_id")
  OrderEntity order;
}
