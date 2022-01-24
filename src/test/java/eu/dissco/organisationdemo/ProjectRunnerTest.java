package eu.dissco.organisationdemo;

import static eu.dissco.organisationdemo.TestUtils.testCsvOrganisation;
import static eu.dissco.organisationdemo.TestUtils.testGbifOrganisation;
import static eu.dissco.organisationdemo.TestUtils.testMinimalOrganisation;
import static eu.dissco.organisationdemo.TestUtils.testROROrganisation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import eu.dissco.organisationdemo.properties.ApplicationProperties;
import eu.dissco.organisationdemo.service.CordraService;
import eu.dissco.organisationdemo.service.CsvOrganisationService;
import eu.dissco.organisationdemo.service.GBIFService;
import eu.dissco.organisationdemo.service.MappingService;
import eu.dissco.organisationdemo.service.RORService;
import java.util.List;
import net.dona.doip.client.DoipClient;
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
  private MappingService mappingService;

  @Mock
  private CordraService cordraService;

  @Mock
  private RORService rorService;

  @Mock
  private GBIFService gbifService;

  @Mock
  private DoipClient doipClient;

  @Mock
  private ApplicationProperties properties;

  private ProjectRunner projectRunner;

  @BeforeEach
  void setup() {
    projectRunner = new ProjectRunner(csvService, mappingService, cordraService, rorService,
        gbifService, doipClient, properties);
  }

  @Test
  void testDeleteAll() {
    // Given
    given(properties.isDeleteAll()).willReturn(true);

    // When
    projectRunner.run();

    // Then
    then(cordraService).should().deleteAll();
  }

  @Test
  void testProcessOrganisations() {
    // Given
    given(properties.isDeleteAll()).willReturn(false);
    given(csvService.retrieveOrganisations(any())).willReturn(List.of(testCsvOrganisation()));
    given(mappingService.mapToDO(testCsvOrganisation())).willReturn(testMinimalOrganisation());
    given(rorService.addROR(testMinimalOrganisation())).willReturn(testROROrganisation());
    given(gbifService.addGbif(testROROrganisation())).willReturn(testGbifOrganisation());

    // When
    projectRunner.run();

    // Then
    then(cordraService).should().processItems(eq(testGbifOrganisation()));
  }

  @Test
  void testProcessNoOrganisations() {
    // Given
    given(properties.isDeleteAll()).willReturn(false);
    given(csvService.retrieveOrganisations(any())).willReturn(List.of());

    // When
    projectRunner.run();

    // Then
    then(cordraService).shouldHaveNoInteractions();
    then(mappingService).shouldHaveNoInteractions();
    then(rorService).shouldHaveNoInteractions();
    then(gbifService).shouldHaveNoInteractions();
  }

}
