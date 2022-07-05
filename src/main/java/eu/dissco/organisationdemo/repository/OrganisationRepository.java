package eu.dissco.organisationdemo.repository;

import static eu.dissco.organisationdemo.database.jooq.Tables.ORGANISATION_DO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.organisationdemo.domain.Organisation;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.Query;
import org.jooq.Record1;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrganisationRepository {

  private final DSLContext context;
  private final ObjectMapper mapper;

  private Organisation mapToDOOrganisation(Record1<JSONB> dbRecord) {
    try {
      return mapper.readValue(dbRecord.get(ORGANISATION_DO.DATA).data(), Organisation.class);
    } catch (JsonProcessingException e) {
      log.error("Unable to map organisation", e);
      return null;
    }
  }

  public List<Organisation> getExistingOrganisations(List<String> ids) {
    return context.select(ORGANISATION_DO.DATA).from(ORGANISATION_DO)
        .where(ORGANISATION_DO.ID.in(ids)).fetch(this::mapToDOOrganisation);
  }

  public void upsertOrganisations(List<Organisation> updated) {
    var queryList = new ArrayList<Query>();
    for (Organisation organisation : updated) {
      try {
        var query = context.insertInto(ORGANISATION_DO)
            .set(ORGANISATION_DO.ID, organisation.getRorCode())
            .set(ORGANISATION_DO.ORGANISATION_CODE, organisation.getOrganisationName())
            .set(ORGANISATION_DO.ORGANISATION_NAME, organisation.getOrganisationName())
            .set(ORGANISATION_DO.CITY, organisation.getCity())
            .set(ORGANISATION_DO.COUNTRY, organisation.getCountry())
            .set(ORGANISATION_DO.COUNTRY_CODE, organisation.getCountryCode())
            .set(ORGANISATION_DO.DATA, JSONB.jsonb(mapper.writeValueAsString(organisation)))
            .onConflict(ORGANISATION_DO.ID)
            .doUpdate()
            .set(ORGANISATION_DO.ORGANISATION_CODE, organisation.getOrganisationName())
            .set(ORGANISATION_DO.ORGANISATION_NAME, organisation.getOrganisationName())
            .set(ORGANISATION_DO.CITY, organisation.getCity())
            .set(ORGANISATION_DO.COUNTRY, organisation.getCountry())
            .set(ORGANISATION_DO.COUNTRY_CODE, organisation.getCountryCode())
            .set(ORGANISATION_DO.DATA, JSONB.jsonb(mapper.writeValueAsString(organisation)));
        queryList.add(query);
      } catch (JsonProcessingException ex) {
        log.error("Failed to insert organisation as Json cannot be parsed for object: {}",
            organisation);
      }
    }
    context.batch(queryList).execute();
  }
}
