package eu.dissco.organisationdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Coordinator {

  private String firstName;
  private String lastName;
  private String type;
  private String email;
  private String phoneNumber;
}
