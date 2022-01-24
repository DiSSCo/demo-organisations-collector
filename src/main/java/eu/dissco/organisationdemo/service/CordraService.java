package eu.dissco.organisationdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import eu.dissco.organisationdemo.domain.DOOrganisation;
import eu.dissco.organisationdemo.domain.ExternalIdentifierType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dona.doip.client.AuthenticationInfo;
import net.dona.doip.client.DigitalObject;
import net.dona.doip.client.DoipClient;
import net.dona.doip.client.DoipException;
import net.dona.doip.client.QueryParams;
import net.dona.doip.client.SearchResults;
import net.dona.doip.client.ServiceInfo;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class CordraService {

  private static final String CONTENT = "content";
  private final DoipClient cordra;
  private final AuthenticationInfo authenticationInfo;
  private final ServiceInfo serviceInfo;
  private final ObjectMapper mapper;

  public void processItems(DOOrganisation organisation) {
    var organisationName = organisation.getOrganisationName();
    var query = buildQuery(organisation);
    try (var result = cordra.search(serviceInfo.serviceId, query, new QueryParams(0, 1),
        authenticationInfo, serviceInfo)) {
      if (result.size() > 0) {
        checkWithExistingObject(organisation, result);
      } else {
        log.info("No record for this organisation found, adding organisation: {}",
            organisationName);
        saveItems(organisation);
      }
    } catch (DoipException e) {
      log.error("Failed to check if organisation: {} is already present", organisationName, e);
    }
  }

  private String buildQuery(DOOrganisation organisation) {
    var builder = new StringBuilder()
        .append("/organisation_name:\"")
        .append(organisation.getOrganisationName())
        .append("\" AND /mainAddress/country/countryName: \"")
        .append(organisation.getMainAddress().getCountry().getCountryName()).append("\"");
    if (organisation.getExternalIdentifiers().containsKey(ExternalIdentifierType.GBIF)){
      builder.append("AND /externalIdentifiers/GBIF/identifier:\"")
          .append(organisation.getExternalIdentifiers().get(
              ExternalIdentifierType.GBIF).getIdentifier())
          .append("\"");
    }
    return builder.toString();
  }

  private void checkWithExistingObject(DOOrganisation organisation,
      SearchResults<DigitalObject> result) {
    log.info("Organisation already present in Cordra");
    try {
      var existingJson = result.iterator().next();
      var existingOrganisation = mapper.readValue(existingJson.attributes.get(CONTENT).toString(),
          DOOrganisation.class);
      if (organisation.equals(existingOrganisation)) {
        log.info("New information equal to stored information, no action needed");
      } else {
        log.info("New information differs from stored information, updating necessary");
        updateExistingObject(organisation, existingJson);
      }
    } catch (JsonProcessingException e) {
      log.warn("Parsing exception", e);
    }
  }

  private void updateExistingObject(DOOrganisation organisation, DigitalObject existingJson)
      throws JsonProcessingException {
    var cordraObject = new DigitalObject();
    cordraObject.type = existingJson.type;
    cordraObject.id = existingJson.id;
    cordraObject.setAttribute(CONTENT, JsonParser.parseString(
        mapper.writeValueAsString(organisation)));
    try {
      var response = cordra.update(cordraObject, authenticationInfo, serviceInfo);
      log.info("Successfully updated object into cordra: {}", response.id);
    } catch (DoipException e) {
      log.error("Failed to update cordra update with id: {}", existingJson.id, e);
    }
  }

  private void saveItems(DOOrganisation organisation) {
    var cordraObject = new DigitalObject();
    cordraObject.type = "Organisation";
    try {
      cordraObject.setAttribute(CONTENT, JsonParser.parseString(
          mapper.writeValueAsString(organisation)));
      var response = cordra.create(cordraObject, authenticationInfo, serviceInfo);
      log.info("Successfully inserted object into cordra: {}", response.id);
    } catch (DoipException | JsonProcessingException e) {
      log.error("Failed to insert OpenDS object: {}", cordraObject.attributes, e);
    }
  }

  public void deleteAll() {
    try {
      var result = cordra.searchIds(serviceInfo.serviceId, "type:\"Organisation\"",
          QueryParams.DEFAULT, authenticationInfo, serviceInfo);
      result.iterator().forEachRemaining(id -> {
        try {
          log.info("Delete object with id: {}", id);
          cordra.delete(id, authenticationInfo, serviceInfo);
        } catch (DoipException e) {
          log.error("Failed to delete id: {}", id, e);
        }
      });
    } catch (DoipException e) {
      log.error("Failed to retrieve all id's for type: Organisation");
    }
  }
}
