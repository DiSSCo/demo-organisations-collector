package eu.dissco.organisationdemo.service;

import eu.dissco.organisationdemo.domain.Organisation;
import eu.dissco.organisationdemo.repository.OrganisationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisationService {

  private final OrganisationRepository repository;

  public void processOrganisations(List<Organisation> organisations) {
    var existingOrganisations = repository.getExistingOrganisations(
        organisations.stream().map(Organisation::getRorCode).toList());
    var updated = organisations.stream().filter(o -> !existingOrganisations.contains(o)).toList();
    repository.upsertOrganisations(updated);
  }

}
