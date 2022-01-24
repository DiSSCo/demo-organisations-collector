package eu.dissco.organisationdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.organisationdemo.client.GbifClient;
import eu.dissco.organisationdemo.domain.Coordinator;
import eu.dissco.organisationdemo.domain.DOOrganisation;
import eu.dissco.organisationdemo.domain.ExternalIdentifierType;
import feign.FeignException.FeignClientException;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class GBIFService {

  private static final String DESCRIPTION = "description";
  private static final String LOGO_URL = "logoUrl";
  private static final String POSTAL_CODE = "postalCode";
  private static final String ADDRESS = "address";
  private static final String CONTACTS = "contacts";
  private static final String FIRST_NAME = "firstName";
  private static final String LAST_NAME = "lastName";
  private static final String TYPE = "type";

  private final GbifClient client;
  private final ObjectMapper mapper;

  public DOOrganisation addGbif(DOOrganisation organisation) {
    if (organisation.getExternalIdentifiers().containsKey(ExternalIdentifierType.GBIF)) {
      log.info("Retrieving Gbif information for organisation: {}",
          organisation.getOrganisationName());
      try {
        var response = mapper.readTree(client.requestGbifInformation(
            organisation.getExternalIdentifiers().get(ExternalIdentifierType.GBIF)
                .getIdentifier()));
        return getGbifInformationFromJson(response, organisation);
      } catch (FeignClientException | JsonProcessingException e) {
        log.warn("Failed to retrieve Gbif with Gbif id: {} with error code: {}",
            organisation.getOrganisationName(), e);
        return organisation;
      }
    } else {
      log.info("No Gbif id available for organisation: {}", organisation.getOrganisationName());
      return organisation;
    }
  }

  private DOOrganisation getGbifInformationFromJson(JsonNode json,
      DOOrganisation organisation) {
    if (json.has(DESCRIPTION)) {
      organisation.setDescription(json.get(DESCRIPTION).asText());
    }
    if (json.has(LOGO_URL)) {
      organisation.setLogo(json.get(LOGO_URL).asText());
    }
    if (json.has(CONTACTS) && !json.get(CONTACTS).isEmpty()) {
      var coordinators = new ArrayList<Coordinator>();
      for (var contact : json.get(CONTACTS)) {
        Coordinator coordinator = getCoordinator(contact);
        coordinators.add(coordinator);
      }
      organisation.setCoordinators(coordinators);
    }
    if (json.has(POSTAL_CODE)) {
      organisation.getMainAddress().setPostalCode(json.get(POSTAL_CODE).asText());
    }
    if (json.has(ADDRESS) && !json.get(ADDRESS).isEmpty()) {
      organisation.getMainAddress().setStreetAddress(json.get(ADDRESS).get(0).asText());
    }
    return organisation;
  }

  private Coordinator getCoordinator(JsonNode contact) {
    var coordinator = Coordinator.builder();
    if (contact.has(FIRST_NAME)) {
      coordinator.firstName(contact.get(FIRST_NAME).asText());
    } else {
      log.warn("No firstname present for contact from Gbif");
    }
    if (contact.has(LAST_NAME)) {
      coordinator.lastName(contact.get(LAST_NAME).asText());
    } else {
      log.warn("No lastName present for contact from Gbif");
    }
    if (contact.has(TYPE)) {
      coordinator.type(contact.get(TYPE).asText());
    } else {
      log.warn("No type present for contact from Gbif");
    }
    return coordinator.build();
  }
}
