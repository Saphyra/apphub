package com.github.saphyra.apphub.service.platform.event_gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationClient;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.apphub.test.common.TestConstants;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class EventGatewayControllerTestIt {
    private static final String SERVICE_NAME = "test-service";
    private static final String TEST_EVENT_URL_1 = "/event/test/1";
    private static final String TEST_EVENT_URL_2 = "/event/test/2";
    private static final String TEST_EVENT_NAME_1 = "test-event-1";
    private static final String TEST_EVENT_NAME_2 = "test-event-2";
    private static final String TEST_VALUE = "test-value";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String ASSEMBLED_EVENT_URL = "http://" + SERVICE_NAME + TEST_EVENT_URL_1;
    private static final RegisterProcessorRequest REGISTER_PROCESSOR_REQUEST = RegisterProcessorRequest.builder()
        .serviceName(SERVICE_NAME)
        .url(TEST_EVENT_URL_1)
        .eventName(TEST_EVENT_NAME_1)
        .build();
    private static final String INVALID_PARAM_LOCALIZED_MESSAGE = "invalid-param-localized-message";

    @Autowired
    private EventProcessorDao eventProcessorDao;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private LocalizationClient localizationClient;

    @LocalServerPort
    private int serverPort;

    @Captor
    ArgumentCaptor<HttpEntity<SendEventRequest<?>>> argumentCaptor;

    @Before
    public void setUp() {
        given(localizationClient.translate("INVALID_PARAM", "hu")).willReturn(INVALID_PARAM_LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        eventProcessorDao.deleteAll();
    }

    @Test
    public void registerProcessor_blankServiceName() throws Exception {
        Response response = sendRegisterProcessorRequest(REGISTER_PROCESSOR_REQUEST.toBuilder().serviceName(" ").build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(INVALID_PARAM_LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("serviceName")).isEqualTo("must not be null or blank");
    }

    @Test
    public void registerProcessor_blankEventName() throws Exception {
        Response response = sendRegisterProcessorRequest(REGISTER_PROCESSOR_REQUEST.toBuilder().eventName(" ").build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(INVALID_PARAM_LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("eventName")).isEqualTo("must not be null or blank");
    }

    @Test
    public void registerProcessor_nullUrl() throws Exception {
        Response response = sendRegisterProcessorRequest(REGISTER_PROCESSOR_REQUEST.toBuilder().url(null).build());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(INVALID_PARAM_LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("url")).isEqualTo("must not be null");
    }

    @Test
    public void registerProcessor_createNew() throws Exception {
        EventProcessor eventProcessor = registerProcessor();
        EventProcessor newProcessor = registerProcessor(REGISTER_PROCESSOR_REQUEST.toBuilder().eventName(TEST_EVENT_NAME_2).build());

        assertThat(newProcessor).isNotEqualTo(eventProcessor);
        assertThat(eventProcessorDao.findAll()).containsExactlyInAnyOrder(eventProcessor, newProcessor);
    }

    @Test
    public void registerProcessor_updateExisting() throws Exception {
        EventProcessor eventProcessor = registerProcessor();
        EventProcessor newProcessor = registerProcessor(REGISTER_PROCESSOR_REQUEST.toBuilder().url(TEST_EVENT_URL_2).build());

        assertThat(newProcessor.getEventProcessorId()).isEqualTo(eventProcessor.getEventProcessorId());
        assertThat(newProcessor.getServiceName()).isEqualTo(eventProcessor.getServiceName());
        assertThat(newProcessor.getEventName()).isEqualTo(eventProcessor.getEventName());
        assertThat(newProcessor.getUrl()).isEqualTo(TEST_EVENT_URL_2);
        assertThat(newProcessor.getLastAccess()).isAfter(eventProcessor.getLastAccess());
        assertThat(eventProcessorDao.findAll()).containsExactly(newProcessor);
    }

    @Test
    public void heartbeat() throws Exception {
        EventProcessor processor = registerProcessor();

        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.HEARTBEAT), SERVICE_NAME)
            .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(eventProcessorDao.findById(processor.getEventProcessorId().toString()).get().getLastAccess()).isAfter(processor.getLastAccess());
    }

    @Test
    public void sendEvent_nullMetadata() throws JsonProcessingException {
        TestEvent payload = new TestEvent(TEST_VALUE);
        SendEventRequest<TestEvent> request = SendEventRequest.<TestEvent>builder()
            .eventName(TEST_EVENT_NAME_1)
            .payload(payload)
            .metadata(null)
            .build();

        Response response = getSendEventResponse(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(INVALID_PARAM_LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("metadata")).isEqualTo("must not be null");
    }

    @Test
    public void sendEvent_blankEventName() throws JsonProcessingException {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(KEY, VALUE);
        TestEvent payload = new TestEvent(TEST_VALUE);
        SendEventRequest<TestEvent> request = SendEventRequest.<TestEvent>builder()
            .eventName(" ")
            .payload(payload)
            .metadata(metadata)
            .build();

        Response response = getSendEventResponse(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(INVALID_PARAM_LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("eventName")).isEqualTo("must not be null or blank");
    }

    @Test
    public void sendEvent() throws Exception {
        LocalDateTime registerTime = registerProcessor().getLastAccess();
        sendEvent(registerTime);
    }

    private EventProcessor registerProcessor() throws Exception {
        return registerProcessor(REGISTER_PROCESSOR_REQUEST);
    }

    private EventProcessor registerProcessor(RegisterProcessorRequest registerProcessorRequest) throws Exception {
        Response result = sendRegisterProcessorRequest(registerProcessorRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        Optional<EventProcessor> eventProcessor = eventProcessorDao.findByServiceNameAndEventName(registerProcessorRequest.getServiceName(), registerProcessorRequest.getEventName());
        assertThat(eventProcessor).isNotEmpty();
        return eventProcessor.get();
    }

    private Response sendRegisterProcessorRequest(RegisterProcessorRequest request) throws Exception {
        return RequestFactory.createRequest()
            .body(objectMapper.writeValueAsString(request))
            .put(UrlFactory.create(serverPort, Endpoints.REGISTER_PROCESSOR))
            .thenReturn();
    }

    private void sendEvent(LocalDateTime registerTime) throws Exception {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(KEY, VALUE);
        TestEvent payload = new TestEvent(TEST_VALUE);
        SendEventRequest<TestEvent> request = SendEventRequest.<TestEvent>builder()
            .eventName(TEST_EVENT_NAME_1)
            .payload(payload)
            .metadata(metadata)
            .build();

        sendEvent(registerTime, request);
    }

    private void sendEvent(LocalDateTime registerTime, SendEventRequest<TestEvent> request) throws com.fasterxml.jackson.core.JsonProcessingException {
        Response result = getSendEventResponse(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        Optional<EventProcessor> eventProcessor = eventProcessorDao.findByServiceNameAndEventName(SERVICE_NAME, TEST_EVENT_NAME_1);
        assertThat(eventProcessor).isNotEmpty();
        assertThat(eventProcessor.get().getLastAccess()).isAfter(registerTime);

        verify(restTemplate).postForEntity(eq(ASSEMBLED_EVENT_URL), argumentCaptor.capture(), eq(Void.class));
        assertThat(argumentCaptor.getValue().getHeaders().get(Constants.LOCALE_HEADER)).containsExactly(TestConstants.DEFAULT_LOCALE);
        assertThat(objectMapper.writeValueAsString(argumentCaptor.getValue().getBody())).isEqualTo(objectMapper.writeValueAsString(request));
    }

    private Response getSendEventResponse(SendEventRequest<TestEvent> request) throws JsonProcessingException {
        return RequestFactory.createRequest()
            .body(objectMapper.writeValueAsString(request))
            .post(UrlFactory.create(serverPort, Endpoints.SEND_EVENT));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TestEvent {
        private String value;
    }
}