package it.montano.multipersistencebackend.order.model;

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

  @Column(nullable = false)
  UUID productId;

  @Column(nullable = false)
  Integer quantity;

  @Column(nullable = false)
  Double price;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  OrderEntity order;
}
