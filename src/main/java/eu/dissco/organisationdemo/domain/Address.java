package eu.dissco.organisationdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

  private Country country;
  private Coordinates coordinates;
  private String city;
  private String postalCode;
  private String streetAddress;
}
