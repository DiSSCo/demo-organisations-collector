package eu.dissco.organisationdemo.service;

import static eu.dissco.organisationdemo.TestUtils.givenOrganisation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import eu.dissco.organisationdemo.repository.OrganisationRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganisationServiceTest {

  @Mock
  private OrganisationRepository repository;

  private OrganisationService service;

  @BeforeEach
  void setup() {
    this.service = new OrganisationService(repository);
  }

  @Test
  void testProcessOrganisation() {
    // Given
    given(repository.getExistingOrganisations(any())).willReturn(List.of());

    // When
    service.processOrganisations(List.of(givenOrganisation()));

    // Then
    then(repository).should().upsertOrganisations(List.of(givenOrganisation()));
  }

  @Test
  void testProcessOrganisationUnchanged() {
    // Given
    given(repository.getExistingOrganisations(any())).willReturn(List.of(givenOrganisation()));

    // When
    service.processOrganisations(List.of(givenOrganisation()));

    // Then
    then(repository).should().upsertOrganisations(List.of());
  }

  @Test
  void testProcessOrganisationChanged() {
    // Given
    given(repository.getExistingOrganisations(any())).willReturn(
        List.of(givenOrganisation("Test")));

    // When
    service.processOrganisations(List.of(givenOrganisation()));

    // Then
    then(repository).should().upsertOrganisations(List.of(givenOrganisation()));
  }

}
