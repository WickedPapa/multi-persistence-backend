package it.montano.sqlvsnosql.product.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Column(unique = true)
  String name;

  Double price;
}
