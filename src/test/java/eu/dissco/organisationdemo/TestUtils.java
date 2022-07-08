package eu.dissco.organisationdemo;

import eu.dissco.organisationdemo.domain.Organisation;

public class TestUtils {

  public static Organisation givenOrganisation(String organisationName) {
    var organisation = new Organisation();
    organisation.setRorCode("01r9htc13");
    organisation.setOrganisationName(organisationName);
    organisation.setCity("Brussels");
    organisation.setCountry("Belgium");
    organisation.setCountryCode("BE");
    return organisation;
  }

  public static Organisation givenOrganisation() {
    return givenOrganisation("Université Libre de Bruxelles, Brussels");
  }

}
