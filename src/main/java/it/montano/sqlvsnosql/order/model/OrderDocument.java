package it.montano.sqlvsnosql.order.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDocument {

  @Id UUID id;
  UserEmbedded user;
  List<OrderItemDocument> items = new ArrayList<>();
  Double total;
}
