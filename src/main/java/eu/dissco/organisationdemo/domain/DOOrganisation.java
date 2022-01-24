package eu.dissco.organisationdemo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.EnumMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class DOOrganisation {

  @JsonProperty("organisation_name")
  private String organisationName;
  private String website;
  private String description;
  private String logo;
  private Address mainAddress;
  private List<Coordinator> coordinators;
  private EnumMap<ExternalIdentifierType, ExternalIdentifier> externalIdentifiers;
  private String remarks;
}
