package eu.dissco.organisationdemo.service;

import static eu.dissco.organisationdemo.TestUtils.testCsvOrganisation;
import static eu.dissco.organisationdemo.TestUtils.testMinimalOrganisation;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MappingServiceTest {

  private MappingService service;

  @BeforeEach
  void setup() {
    service = new MappingService();
  }

  @Test
  void testMapToDO() {
    // Given

    // When
    var organisation = service.mapToDO(testCsvOrganisation());

    // Then
    assertThat(organisation).isEqualTo(testMinimalOrganisation());
  }



}
