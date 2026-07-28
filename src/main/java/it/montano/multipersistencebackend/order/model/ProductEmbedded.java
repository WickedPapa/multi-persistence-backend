package it.montano.multipersistencebackend.order.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductEmbedded {
  UUID productId;
  String name;

  @Field(targetType = FieldType.DECIMAL128)
  BigDecimal price;
}
