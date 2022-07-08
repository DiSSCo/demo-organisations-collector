package eu.dissco.organisationdemo;

import eu.dissco.organisationdemo.properties.ApplicationProperties;
import eu.dissco.organisationdemo.service.CsvOrganisationService;
import eu.dissco.organisationdemo.service.OrganisationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ProjectRunner implements CommandLineRunner {

  private final CsvOrganisationService csvService;
  private final ApplicationProperties properties;
  private final OrganisationService service;

  @Override
  public void run(String... args) {
    log.info("Starting application");
    log.info("Retrieving organisations from file");
    var csvOrganisations = csvService.retrieveOrganisations(properties.getFilename());
    log.info("Retrieved a number of: {} organisations from csv", csvOrganisations.size());
    service.processOrganisations(csvOrganisations);
  }

}
