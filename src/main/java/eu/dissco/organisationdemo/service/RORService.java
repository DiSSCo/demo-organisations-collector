package eu.dissco.organisationdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.organisationdemo.client.RorClient;
import eu.dissco.organisationdemo.domain.Address;
import eu.dissco.organisationdemo.domain.Coordinates;
import eu.dissco.organisationdemo.domain.Country;
import eu.dissco.organisationdemo.domain.DOOrganisation;
import eu.dissco.organisationdemo.domain.ExternalIdentifier;
import eu.dissco.organisationdemo.domain.ExternalIdentifierType;
import feign.FeignException.FeignClientException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RORService {

  private static final String LINKS = "links";
  private static final String COUNTRY = "country";
  private static final String EXTERNAL_IDS = "external_ids";
  private static final String WIKIDATA = "Wikidata";
  private static final int FIRST = 0;

  private final RorClient client;
  private final ObjectMapper mapper;

  public DOOrganisation addROR(DOOrganisation organisation) {
    if (organisation.getExternalIdentifiers().containsKey(ExternalIdentifierType.ROR)) {
      log.info("Retrieving ROR information for organisation: {}",
          organisation.getOrganisationName());
      try {
        var response = mapper.readTree(client.requestRORInformation(
            organisation.getExternalIdentifiers().get(ExternalIdentifierType.ROR).getIdentifier()));
        return getRORInformationFromJson(response, organisation);
      } catch (FeignClientException | JsonProcessingException e) {
        log.warn("Failed to retrieve ROR with ROR id: {} with error code: {}",
            organisation.getOrganisationName(), e);
        return organisation;
      }
    } else {
      log.info("No ROR id available for organisation: {}", organisation.getOrganisationName());
      return organisation;
    }
  }

  private DOOrganisation getRORInformationFromJson(JsonNode json, DOOrganisation organisation) {
    if (json.has("errors")) {
      log.error("Response contains errors field containing: {}", json.toPrettyString());
      return organisation;
    }
    if (json.has(LINKS) && !json.get(LINKS).isEmpty()) {
      organisation.setWebsite(json.get(LINKS).get(FIRST).asText());
    }
    var jsonAddressNode = json.get("addresses").get(0);
    var address = Address.builder()
        .city(jsonAddressNode.get("city").asText())
        .coordinates(Coordinates.builder()
            .latitude(jsonAddressNode.get("lat").asDouble())
            .longitude(jsonAddressNode.get("lng").asDouble())
            .build())
        .country(Country.builder()
            .countryCode(json.get(COUNTRY).get("country_code").asText())
            .countryName(json.get(COUNTRY).get("country_name").asText())
            .build())
        .build();
    organisation.setMainAddress(address);
    if (json.has(EXTERNAL_IDS) && json.get(EXTERNAL_IDS).has(WIKIDATA)) {
      var identifier = json.get(EXTERNAL_IDS).get(WIKIDATA).get("all").get(FIRST).asText();
      organisation.getExternalIdentifiers()
          .put(ExternalIdentifierType.WIKIDATA, ExternalIdentifier.builder()
              .identifier(identifier)
              .url("https://www.wikidata.org/wiki/" + identifier)
              .build());
    }
    return organisation;
  }
}
