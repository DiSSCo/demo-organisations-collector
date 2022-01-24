package eu.dissco.organisationdemo.service;

import eu.dissco.organisationdemo.domain.Address;
import eu.dissco.organisationdemo.domain.Country;
import eu.dissco.organisationdemo.domain.CsvOrganisation;
import eu.dissco.organisationdemo.domain.DOOrganisation;
import eu.dissco.organisationdemo.domain.ExternalIdentifier;
import eu.dissco.organisationdemo.domain.ExternalIdentifierType;
import java.util.EnumMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MappingService {

  public DOOrganisation mapToDO(CsvOrganisation csvRecord) {
    var externalIdentifiers = mapExternalIdentifiers(csvRecord);
    return DOOrganisation.builder()
        .organisationName(csvRecord.getOrganisationName())
        .mainAddress(
            Address.builder().country(Country.builder().countryName(csvRecord.getCountry()).build())
                .build())
        .externalIdentifiers(externalIdentifiers)
        .remarks(csvRecord.getRemarks())
        .build();
  }

  private EnumMap<ExternalIdentifierType, ExternalIdentifier> mapExternalIdentifiers(
      CsvOrganisation csvRecord) {
    var externalIdentifiers = new EnumMap<ExternalIdentifierType, ExternalIdentifier>(
        ExternalIdentifierType.class);
    if (csvRecord.getGbifCode() != null && !csvRecord.getGbifCode().isEmpty()) {
      var gbifId = csvRecord.getGbifCode();
      log.debug("GBIF identifier found: {} adding to external identifiers", gbifId);
      var gbifIdentifier = ExternalIdentifier.builder()
          .identifier(gbifId)
          .url("https://www.gbif.org/publisher/" + gbifId)
          .build();
      externalIdentifiers.put(ExternalIdentifierType.GBIF, gbifIdentifier);
    }
    if (csvRecord.getRorCode() != null && !csvRecord.getRorCode().isEmpty()) {
      var rorId = csvRecord.getRorCode();
      log.debug("ROR identifier found: {} adding to external identifiers", rorId);
      var rorIdentifier = ExternalIdentifier.builder()
          .identifier(rorId)
          .url("https://ror.org/" + rorId)
          .build();
      externalIdentifiers.put(ExternalIdentifierType.ROR, rorIdentifier);
    }
    return externalIdentifiers;
  }

}
