package it.montano.multipersistencebackend.order.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemDocument {

  ProductEmbedded productEmbedded;
  Integer quantity;
}
