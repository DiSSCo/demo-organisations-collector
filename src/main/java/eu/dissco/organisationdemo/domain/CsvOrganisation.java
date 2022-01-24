package eu.dissco.organisationdemo.domain;

import com.univocity.parsers.annotations.Parsed;
import lombok.Data;

@Data
public class CsvOrganisation {

  @Parsed(index = 0)
  String country;
  @Parsed(index = 3)
  String organisationName;
  @Parsed(index = 5)
  String rorCode;
  @Parsed(index = 8)
  String gbifCode;
  @Parsed(index = 11)
  String remarks;
}
