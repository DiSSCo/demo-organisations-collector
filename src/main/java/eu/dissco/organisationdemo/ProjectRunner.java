package eu.dissco.organisationdemo;

import eu.dissco.organisationdemo.domain.CsvOrganisation;
import eu.dissco.organisationdemo.properties.ApplicationProperties;
import eu.dissco.organisationdemo.service.CordraService;
import eu.dissco.organisationdemo.service.CsvOrganisationService;
import eu.dissco.organisationdemo.service.GBIFService;
import eu.dissco.organisationdemo.service.MappingService;
import eu.dissco.organisationdemo.service.RORService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dona.doip.client.DoipClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ProjectRunner implements CommandLineRunner {

  private final CsvOrganisationService csvService;
  private final MappingService mappingService;
  private final CordraService cordraService;
  private final RORService rorService;
  private final GBIFService gbifService;
  private final DoipClient cordra;
  private final ApplicationProperties properties;

  @Override
  public void run(String... args) {
    if (properties.isDeleteAll()) {
      cordraService.deleteAll();
    } else {
      log.info("Starting application");
      log.info("Retrieving organisations from file");
      var csvOrganisations = csvService.retrieveOrganisations(properties.getFilename());
      log.info("Retrieved a number of: {} organisations from csv", csvOrganisations.size());
      for (var csvOrganisation : csvOrganisations) {
        processOrganisation(csvOrganisation);
      }
    }
    cordra.close();
    log.info("Successfully processed all organisations");
  }

  void processOrganisation(CsvOrganisation csvOrganisation) {
    var organisation = mappingService.mapToDO(csvOrganisation);
    log.info("Processing organisation: {}", csvOrganisation.getOrganisationName());
    organisation = rorService.addROR(organisation);
    organisation = gbifService.addGbif(organisation);
    cordraService.processItems(organisation);
  }
}
