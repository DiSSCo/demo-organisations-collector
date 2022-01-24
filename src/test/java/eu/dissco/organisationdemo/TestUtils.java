package eu.dissco.organisationdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import eu.dissco.organisationdemo.domain.Address;
import eu.dissco.organisationdemo.domain.Coordinates;
import eu.dissco.organisationdemo.domain.Coordinator;
import eu.dissco.organisationdemo.domain.Country;
import eu.dissco.organisationdemo.domain.CsvOrganisation;
import eu.dissco.organisationdemo.domain.DOOrganisation;
import eu.dissco.organisationdemo.domain.ExternalIdentifier;
import eu.dissco.organisationdemo.domain.ExternalIdentifierType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import net.dona.doip.client.DigitalObject;
import org.springframework.core.io.ClassPathResource;

public class TestUtils {

  private static final String ORGANISATION_NAME = "Senckenberg Society for Nature Research";
  private static final String REMARKS = "This is a remark";
  private static final String COUNTRY_NAME = "Germany";
  private static final String COUNTRY_CODE = "DE";
  private static final double LONGITUDE = 8.65168;
  private static final double LATITUDE = 50.117646;
  private static final String CITY = "Frankfurt am Main";
  private static final String WEBSITE = "http://www.senckenberg.de/";
  private static final String ROR_ID = "00xmqmx64";
  private static final String ROR_URL = "https://ror.org/00xmqmx64";
  private static final String GBIF_ID = "c76cf030-2a95-11da-9cc1-b8a03c50a862";
  private static final String GBIF_URL = "https://www.gbif.org/publisher/c76cf030-2a95-11da-9cc1-b8a03c50a862";
  private static final String WIKIDATA_ID = "Q880568";
  private static final String WIKIDATA_URL = "https://www.wikidata.org/wiki/Q880568";
  private static final String LOGO_URL = "https://getlogo.net/wp-content/uploads/2020/01/senckenberg-museum-frankfurt-logo-vector.png";
  private static final String CONTACT_TYPE = "TECHNICAL_POINT_OF_CONTACT";
  private static final String FIRST_NAME = "Andreas";
  private static final String LAST_NAME = "Allspach";
  private static final String DESCRIPTION = "http://www.senckenberg.de/";

  public static String loadResourceFile(String fileName) throws IOException {
    return new String(new ClassPathResource(fileName).getInputStream().readAllBytes(),
        StandardCharsets.UTF_8);
  }

  public static CsvOrganisation testCsvOrganisation() {
    var csvOrganisation = new CsvOrganisation();
    csvOrganisation.setOrganisationName(ORGANISATION_NAME);
    csvOrganisation.setCountry(COUNTRY_NAME);
    csvOrganisation.setGbifCode(GBIF_ID);
    csvOrganisation.setRorCode(ROR_ID);
    csvOrganisation.setRemarks(REMARKS);
    return csvOrganisation;
  }

  public static DOOrganisation testMinimalOrganisation() {
    return testMinimalOrganisation(getExternalIds());
  }

  public static DOOrganisation testMinimalOrganisation(
      EnumMap<ExternalIdentifierType, ExternalIdentifier> externalIds) {
    return DOOrganisation.builder().organisationName(ORGANISATION_NAME).mainAddress(
            Address.builder().country(Country.builder().countryName(COUNTRY_NAME).build()).build())
        .remarks(REMARKS).externalIdentifiers(externalIds).build();
  }

  public static EnumMap<ExternalIdentifierType, ExternalIdentifier> getExternalIds() {
    var externalIdentifiers = new EnumMap<ExternalIdentifierType, ExternalIdentifier>(
        ExternalIdentifierType.class);
    externalIdentifiers.put(ExternalIdentifierType.ROR,
        ExternalIdentifier.builder().identifier(ROR_ID).url(ROR_URL).build());
    externalIdentifiers.put(ExternalIdentifierType.GBIF,
        ExternalIdentifier.builder().identifier(GBIF_ID).url(GBIF_URL).build());
    return externalIdentifiers;
  }

  public static DOOrganisation testROROrganisation() {
    var organisation = testMinimalOrganisation(getExternalIds());
    organisation.setMainAddress(Address.builder()
        .country(Country.builder().countryName(COUNTRY_NAME).countryCode(COUNTRY_CODE).build())
        .city(CITY)
        .coordinates(Coordinates.builder().latitude(LATITUDE).longitude(LONGITUDE).build())
        .build());
    organisation.getExternalIdentifiers().put(ExternalIdentifierType.WIKIDATA,
        ExternalIdentifier.builder().url(WIKIDATA_URL).identifier(WIKIDATA_ID).build());
    organisation.setWebsite(WEBSITE);
    return organisation;
  }

  public static DOOrganisation testGbifOrganisation() {
    var organisation = testROROrganisation();
    organisation.setLogo(LOGO_URL);
    organisation.setDescription(DESCRIPTION);
    organisation.setCoordinators(List.of(
        Coordinator.builder().type(CONTACT_TYPE).firstName(FIRST_NAME).lastName(LAST_NAME)
            .build()));
    return organisation;
  }

  public static DigitalObject responseCreateDigitalObject() {
    var digitalObject = new DigitalObject();
    digitalObject.id = "1";
    return digitalObject;
  }

  public static DigitalObject responseEqualsDigitalObject() throws JsonProcessingException {
    var digitalObject = new DigitalObject();
    digitalObject.id = "1";
    digitalObject.type = "Organisations";
    digitalObject.setAttribute("content", JsonParser.parseString(
        new ObjectMapper().writeValueAsString(testGbifOrganisation())));
    return digitalObject;
  }

  public static DigitalObject responseUnEqualsDigitalObject() throws JsonProcessingException {
    var digitalObject = new DigitalObject();
    digitalObject.id = "1";
    digitalObject.type = "Organisations";
    var organisation = testGbifOrganisation();
    organisation.setCoordinators(null);
    digitalObject.setAttribute("content", JsonParser.parseString(
        new ObjectMapper().writeValueAsString(organisation)));
    return digitalObject;
  }

  public static DigitalObject invalidJsonObject() {
    var digitalObject = new DigitalObject();
    digitalObject.setAttribute("content", "{{}");
    return digitalObject;
  }
}
