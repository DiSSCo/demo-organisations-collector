package eu.dissco.organisationdemo.properties;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

  @NotBlank
  private String filename = "organisations.csv";
}
