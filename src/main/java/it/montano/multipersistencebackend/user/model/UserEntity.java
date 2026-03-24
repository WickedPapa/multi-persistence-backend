package it.montano.multipersistencebackend.user.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  String firstName;
  String lastName;

  @Column(unique = true)
  String email;
}
