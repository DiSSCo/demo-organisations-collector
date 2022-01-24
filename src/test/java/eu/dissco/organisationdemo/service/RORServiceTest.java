package eu.dissco.organisationdemo.service;

import static eu.dissco.organisationdemo.TestUtils.loadResourceFile;
import static eu.dissco.organisationdemo.TestUtils.testMinimalOrganisation;
import static eu.dissco.organisationdemo.TestUtils.testROROrganisation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.organisationdemo.client.RorClient;
import eu.dissco.organisationdemo.domain.ExternalIdentifierType;
import java.io.IOException;
import java.util.EnumMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RORServiceTest {

  @Mock
  private RorClient client;

  private RORService service;

  @BeforeEach
  void setup() {
    var mapper = new ObjectMapper().findAndRegisterModules();
    service = new RORService(client, mapper);
  }

  @Test
  void testAddRor() throws IOException {
    // Given
    given(client.requestRORInformation(anyString())).willReturn(
        loadResourceFile("ror/organisation.json"));

    // When
    var result = service.addROR(testMinimalOrganisation());

    // Then
    assertThat(result).isEqualTo(testROROrganisation());
  }

  @Test
  void testNoRORId() {
    // Given
    var organisationWithoutExternalIds = testMinimalOrganisation(
        new EnumMap<>(ExternalIdentifierType.class));

    // When
    var result = service.addROR(organisationWithoutExternalIds);

    // Then
    assertThat(result).isEqualTo(organisationWithoutExternalIds);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ror/invalid-json.json", "ror/errors.json"})
  void testFailedRetrieval(String filename) throws IOException {
    // Given
    given(client.requestRORInformation(anyString())).willReturn(
        loadResourceFile(filename));

    // When
    var result = service.addROR(testMinimalOrganisation());

    // Then
    assertThat(result).isEqualTo(testMinimalOrganisation());
  }

}