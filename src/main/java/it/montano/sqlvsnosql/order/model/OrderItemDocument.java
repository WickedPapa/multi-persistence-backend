package it.montano.sqlvsnosql.order.model;

import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemDocument {

  @Id UUID id;
  UUID productId;
  Integer quantity;
  Double unitPrice;
}
