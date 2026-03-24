package it.montano.multipersistencebackend.common.dto;

import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequestDto {
  UUID productId;
  String name;
  Integer quantity;
  Double price;
}
