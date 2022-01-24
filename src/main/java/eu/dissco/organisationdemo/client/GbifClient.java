package eu.dissco.organisationdemo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gbif", url = "https://api.gbif.org/v1/organization/")
public interface GbifClient {

  @GetMapping("/{gbifId}")
  String requestGbifInformation(@PathVariable String gbifId);

}
