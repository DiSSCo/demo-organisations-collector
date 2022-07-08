package eu.dissco.organisationdemo;

import static eu.dissco.organisationdemo.TestUtils.givenOrganisation;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import eu.dissco.organisationdemo.properties.ApplicationProperties;
import eu.dissco.organisationdemo.service.CsvOrganisationService;
import eu.dissco.organisationdemo.service.OrganisationService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectRunnerTest {

  @Mock
  private CsvOrganisationService csvService;
  @Mock
  private OrganisationService service;
  @Mock
  private ApplicationProperties properties;

  private ProjectRunner projectRunner;

  @BeforeEach
  void setup() {
    this.projectRunner = new ProjectRunner(csvService, properties, service);
  }

  @Test
  void testRun() {
    // Given
    given(properties.getFilename()).willReturn("organisations.csv");
    given(csvService.retrieveOrganisations("organisations.csv")).willReturn(
        List.of(givenOrganisation()));

    // When
    projectRunner.run();

    // Then
    then(service).should().processOrganisations(List.of(givenOrganisation()));
  }

}
