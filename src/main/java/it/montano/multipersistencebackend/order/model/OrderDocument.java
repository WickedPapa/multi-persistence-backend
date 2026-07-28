package it.montano.multipersistencebackend.order.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDocument {

  @Id UUID id;
  UserEmbedded user;
  List<OrderItemEmbedded> items = new ArrayList<>();

  @Field(targetType = FieldType.DECIMAL128)
  BigDecimal total;
}
