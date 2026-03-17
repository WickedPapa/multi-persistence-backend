package it.montano.sqlvsnosql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String productId;

  private Integer quantity;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private OrderEntity order;
}
