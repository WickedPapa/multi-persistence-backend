package it.montano.multipersistencebackend.order.model;

import it.montano.multipersistencebackend.user.model.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  UserEntity user;

  @Column(nullable = false, length = 100)
  String userFirstNameSnapshot;

  @Column(nullable = false, length = 100)
  String userLastNameSnapshot;

  @Column(nullable = false)
  String userEmailSnapshot;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  List<OrderItemEntity> items = new ArrayList<>();

  @Column(nullable = false)
  BigDecimal total;
}
