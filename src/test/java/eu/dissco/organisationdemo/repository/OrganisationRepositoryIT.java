package eu.dissco.organisationdemo.repository;

import static eu.dissco.organisationdemo.TestUtils.givenOrganisation;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganisationRepositoryIT extends BaseRepositoryIT {

  private final ObjectMapper mapper = new ObjectMapper();

  private OrganisationRepository repository;

  @BeforeEach
  void setup() {
    repository = new OrganisationRepository(context, mapper);
  }

  @Test
  void testUpsertOrganisationInset() {
    // Given
    var givenOrganisation = givenOrganisation();

    // When
    repository.upsertOrganisations(List.of(givenOrganisation));

    // Then
    var result = repository.getExistingOrganisations(List.of(givenOrganisation.getRorCode()));
    assertThat(result).isEqualTo(List.of(givenOrganisation));
  }

  @Test
  void testUpsertOrganisationUpdate() {
    // Given
    var givenOrganisation = givenOrganisation();
    repository.upsertOrganisations(List.of(givenOrganisation));
    var newName = "New Name";
    var updatedOrganisation = givenOrganisation(newName);

    // When
    repository.upsertOrganisations(List.of(updatedOrganisation));

    // Then
    var result = repository.getExistingOrganisations(List.of(givenOrganisation.getRorCode()));
    assertThat(result.get(0).getOrganisationName()).isEqualTo(newName);
  }


}
