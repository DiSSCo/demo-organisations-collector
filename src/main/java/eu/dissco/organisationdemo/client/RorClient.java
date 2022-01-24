package eu.dissco.organisationdemo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ror", url = "https://api.ror.org/organizations")
public interface RorClient {

  @GetMapping("/{rorId}")
  String requestRORInformation(@PathVariable String rorId);

}
