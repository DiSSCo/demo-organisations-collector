package eu.dissco.organisationdemo.service;

import static eu.dissco.organisationdemo.TestUtils.invalidJsonObject;
import static eu.dissco.organisationdemo.TestUtils.responseCreateDigitalObject;
import static eu.dissco.organisationdemo.TestUtils.responseEqualsDigitalObject;
import static eu.dissco.organisationdemo.TestUtils.responseUnEqualsDigitalObject;
import static eu.dissco.organisationdemo.TestUtils.testGbifOrganisation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dissco.organisationdemo.properties.CordraProperties;
import java.util.List;
import net.dona.doip.client.AuthenticationInfo;
import net.dona.doip.client.DigitalObject;
import net.dona.doip.client.DoipClient;
import net.dona.doip.client.DoipException;
import net.dona.doip.client.QueryParams;
import net.dona.doip.client.SearchResults;
import net.dona.doip.client.ServiceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CordraServiceTest {

  @Mock
  private DoipClient client;

  @Mock
  private SearchResults<DigitalObject> digitalObjectSearchResults;

  @Mock
  private SearchResults<String> stringSearchResults;

  @Mock
  private AuthenticationInfo authenticationInfo;

  @Mock
  private ServiceInfo serviceInfo;

  private CordraService service;

  @BeforeEach
  void setup() {
    service = new CordraService(client, authenticationInfo, serviceInfo,
        new ObjectMapper().findAndRegisterModules());
  }

  @Test
  void testNewOrganisation() throws Exception {
    // Given
    given(digitalObjectSearchResults.size()).willReturn(0);
    given(client.search(any(), anyString(), any(QueryParams.class),
        any(AuthenticationInfo.class), any(ServiceInfo.class))).willReturn(
        digitalObjectSearchResults);
    given(client.create(any(), any(), any())).willReturn(responseCreateDigitalObject());

    // When
    service.processItems(testGbifOrganisation());

    // Then
    then(client).should().create(any(), any(), any());
  }

  @Test
  void testExistingOrganisation() throws Exception {
    // Given
    given(digitalObjectSearchResults.size()).willReturn(1);
    given(client.search(any(), anyString(), any(QueryParams.class),
        any(AuthenticationInfo.class), any(ServiceInfo.class))).willReturn(
        digitalObjectSearchResults);
    given(digitalObjectSearchResults.iterator()).willReturn(
        List.of(responseEqualsDigitalObject()).iterator());

    // When
    service.processItems(testGbifOrganisation());

    // Then
    then(client).shouldHaveNoMoreInteractions();
  }

  @Test
  void testUpdateOrganisation() throws Exception {
    // Given
    given(digitalObjectSearchResults.size()).willReturn(1);
    given(client.search(any(), anyString(), any(QueryParams.class),
        any(AuthenticationInfo.class), any(ServiceInfo.class))).willReturn(
        digitalObjectSearchResults);
    given(digitalObjectSearchResults.iterator()).willReturn(
        List.of(responseUnEqualsDigitalObject()).iterator());
    given(client.update(any(), any(), any())).willReturn(responseCreateDigitalObject());

    // When
    service.processItems(testGbifOrganisation());

    // Then
    then(client).should().update(any(), any(), any());
  }

  @Test
  void testDeleteAll() throws Exception {
    // Given
    given(stringSearchResults.iterator()).willReturn(List.of("1", "2").iterator());
    given(client.searchIds(any(), any(), any(), any(), any())).willReturn(
        stringSearchResults);

    // When
    service.deleteAll();

    // Then
    then(client).should(times(2)).delete(any(), any(), any());
  }

  @Test
  void testExceptionSearch() throws DoipException {
    // Given
    given(client.search(any(), any(), any(), any(), any())).willThrow(DoipException.class);

    // When
    service.processItems(testGbifOrganisation());

    // Then
    then(client).shouldHaveNoMoreInteractions();
  }

  @Test
  void testExceptionSearchIds() throws DoipException {
    // Given
    given(client.searchIds(any(), any(), any(), any(), any())).willThrow(DoipException.class);

    // When
    service.deleteAll();

    // Then
    then(client).shouldHaveNoMoreInteractions();
  }

  @Test
  void testExceptionDelete() throws DoipException {
    // Given
    given(stringSearchResults.iterator()).willReturn(List.of("1").iterator());
    given(client.searchIds(any(), any(), any(), any(), any())).willReturn(
        stringSearchResults);
    doThrow(DoipException.class).when(client).delete(any(), any(), any());

    // When
    service.deleteAll();

    // Then
    then(client).shouldHaveNoMoreInteractions();
  }

  @Test
  void testExceptionCreate() throws DoipException {
    // Given
    given(digitalObjectSearchResults.size()).willReturn(0);
    given(client.search(any(), anyString(), any(QueryParams.class),
        any(AuthenticationInfo.class), any(ServiceInfo.class))).willReturn(
        digitalObjectSearchResults);
    given(client.create(any(), any(), any())).willThrow(DoipException.class);

    // When
    service.processItems(testGbifOrganisation());

    // Then
    then(client).shouldHaveNoMoreInteractions();
  }

  @Test
  void testExceptionUpdate() throws DoipException, JsonProcessingException {
    // Given
    given(digitalObjectSearchResults.size()).willReturn(1);
    given(client.search(any(), anyString(), any(QueryParams.class),
        any(AuthenticationInfo.class), any(ServiceInfo.class))).willReturn(
        digitalObjectSearchResults);
    given(digitalObjectSearchResults.iterator()).willReturn(
        List.of(responseUnEqualsDigitalObject()).iterator());
    given(client.update(any(), any(), any())).willThrow(DoipException.class);

    // When
    service.processItems(testGbifOrganisation());

    // Then
    then(client).shouldHaveNoMoreInteractions();
  }

  @Test
  void testExceptionJson() throws DoipException {
    // Given
    given(digitalObjectSearchResults.size()).willReturn(1);
    given(client.search(any(), anyString(), any(QueryParams.class),
        any(AuthenticationInfo.class), any(ServiceInfo.class))).willReturn(
        digitalObjectSearchResults);
    given(digitalObjectSearchResults.iterator()).willReturn(
        List.of(invalidJsonObject()).iterator());

    // When
    service.processItems(testGbifOrganisation());

    // Then
    then(client).shouldHaveNoMoreInteractions();
  }

}
