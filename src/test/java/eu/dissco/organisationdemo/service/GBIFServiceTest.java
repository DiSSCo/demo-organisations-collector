package eu.dissco.organisationdemo.service;

import static eu.dissco.organisationdemo.TestUtils.loadResourceFile;
import static eu.dissco.organisationdemo.TestUtils.testGbifOrganisation;
import static eu.dissco.organisationdemo.TestUtils.testMinimalOrganisation;
import static eu.dissco.organisationdemo.TestUtils.testROROrganisation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.organisationdemo.client.GbifClient;
import eu.dissco.organisationdemo.domain.ExternalIdentifierType;
import feign.FeignException.FeignClientException;
import java.io.IOException;
import java.util.EnumMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GBIFServiceTest {

  @Mock
  private GbifClient client;

  private GBIFService service;

  @BeforeEach
  void setup() {
    service = new GBIFService(client, new ObjectMapper().findAndRegisterModules());
  }

  @Test
  void testNoGbifId() {
    // Given
    var organisationWithoutExternalIds = testMinimalOrganisation(
        new EnumMap<>(ExternalIdentifierType.class));

    // When
    var result = service.addGbif(organisationWithoutExternalIds);

    // Then
    assertThat(result).isEqualTo(organisationWithoutExternalIds);
  }

  @Test
  void testAddGbif() throws IOException {
    // Given
    given(client.requestGbifInformation(anyString())).willReturn(
        loadResourceFile("gbif/organisation.json"));

    // When
    var result = service.addGbif(testROROrganisation());

    // Then
    assertThat(result).isEqualTo(testGbifOrganisation());
  }

  @Test
  void testFeignException() {
    // Given
    given(client.requestGbifInformation(anyString())).willThrow(FeignClientException.class);

    // When
    var result = service.addGbif(testROROrganisation());

    // Then
    assertThat(result).isEqualTo(testROROrganisation());
  }

}
