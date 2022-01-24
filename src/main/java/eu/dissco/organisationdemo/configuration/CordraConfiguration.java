package eu.dissco.organisationdemo.configuration;

import eu.dissco.organisationdemo.properties.CordraProperties;
import lombok.AllArgsConstructor;
import net.dona.doip.client.AuthenticationInfo;
import net.dona.doip.client.DoipClient;
import net.dona.doip.client.PasswordAuthenticationInfo;
import net.dona.doip.client.ServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class CordraConfiguration {

  private final CordraProperties properties;

  @Bean
  DoipClient doipClient() {
    return new DoipClient();
  }

  @Bean
  AuthenticationInfo authenticationInfo() {
    return new PasswordAuthenticationInfo(properties.getUsername(), properties.getPassword());
  }

  @Bean
  ServiceInfo serviceInfo() {
    return new ServiceInfo(properties.getServiceId(), properties.getIpAddress(),
        properties.getPort());
  }

}
