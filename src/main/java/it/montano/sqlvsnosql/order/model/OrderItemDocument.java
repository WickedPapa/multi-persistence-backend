package it.montano.sqlvsnosql.order.model;

import it.montano.sqlvsnosql.product.model.ProductDocument;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemDocument {

  ProductDocument productDocument;
  Integer quantity;
}
