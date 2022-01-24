package eu.dissco.organisationdemo.service;

import static eu.dissco.organisationdemo.TestUtils.testCsvOrganisation;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CsvOrganisationServiceTest {

  private CsvOrganisationService service;

  @BeforeEach
  void setup() {
    service = new CsvOrganisationService();
  }

  @Test
  void testRetrieveOrganisations() {
    // Given

    // When
    var organisations = service.retrieveOrganisations("csv/organisation.csv");

    // Then
    assertThat(organisations).isEqualTo(List.of(testCsvOrganisation()));
  }

  @Test
  void testRetrieveInvalidOrganisations() {
    // Given

    // When
    var organisations = service.retrieveOrganisations("csv/invalid_organisation.csv");

    // Then
    assertThat(organisations).isEmpty();
  }

}
