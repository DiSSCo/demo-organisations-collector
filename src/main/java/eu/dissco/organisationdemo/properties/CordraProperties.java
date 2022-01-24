package eu.dissco.organisationdemo.properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cordra")
public class CordraProperties {

  @NotBlank
  private String username;
  @NotBlank
  private String password;
  @NotBlank
  private String serviceId;
  @NotBlank
  private String ipAddress;
  @Positive
  private int port;
}
