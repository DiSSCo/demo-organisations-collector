package eu.dissco.organisationdemo.domain;

import com.univocity.parsers.annotations.Parsed;
import lombok.Data;

@Data
public class Organisation {
  @Parsed(index = 0)
  private String rorCode;
  @Parsed(index = 3)
  private String organisationName;
  @Parsed(index = 2)
  private String city;
  @Parsed(index = 4)
  private String country;
  @Parsed(index = 5)
  private String countryCode;
}
