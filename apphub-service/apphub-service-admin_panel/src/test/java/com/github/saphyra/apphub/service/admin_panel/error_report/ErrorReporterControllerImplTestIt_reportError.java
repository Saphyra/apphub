package com.github.saphyra.apphub.service.admin_panel.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.StackTraceModel;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationClient;
import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.ErrorResponseValidator;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.test.common.TestConstants.DEFAULT_LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class ErrorReporterControllerImplTestIt_reportError {
    private static final String MESSAGE = "message";
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final Integer RESPONSE_STATUS = 32;
    private static final String RESPONSE_BODY = "response-body";
    private static final String FILE_NAME = "file-name";
    private static final String CLASS_NAME = "class-name";
    private static final String METHOD_NAME = "method-name";
    private static final Integer LINE_NUMBER = 42;
    private static final String EXCEPTION_TYPE = "exception-type";
    private static final String EXCEPTION_THREAD = "exception-thread";
    private static final String SERVICE = "service";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationClient localizationClient;

    @MockBean
    private MessageSenderApiClient messageSenderClient;

    @Autowired
    private ErrorReportDao errorReportDao;

    @Before
    public void setUp() {
        given(localizationClient.translate(anyString(), eq(DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        errorReportDao.deleteAll();
    }

    @Test
    public void idFilled() {
        ErrorReportModel model = ErrorReportModel.builder()
            .id(UUID.randomUUID())
            .message(MESSAGE)
            .build();

        Response response = RequestFactory.createRequest()
            .body(model)
            .put(UrlFactory.create(serverPort, Endpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR));

        ErrorResponseValidator.verifyInvalidParam(response, "id", "must be null");
    }

    @Test
    public void blankMessage() {
        ErrorReportModel model = ErrorReportModel.builder()
            .message(" ")
            .build();

        Response response = RequestFactory.createRequest()
            .body(model)
            .put(UrlFactory.create(serverPort, Endpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR));

        ErrorResponseValidator.verifyInvalidParam(response, "message", "must not be null or blank");
    }

    @Test
    public void saveReport() {
        StackTraceModel stackTraceModel = StackTraceModel.builder()
            .fileName(FILE_NAME)
            .className(CLASS_NAME)
            .methodName(METHOD_NAME)
            .lineNumber(LINE_NUMBER)
            .build();

        ExceptionModel exceptionModel = ExceptionModel.builder()
            .type(EXCEPTION_TYPE)
            .thread(EXCEPTION_THREAD)
            .stackTrace(Arrays.asList(stackTraceModel))
            .build();

        ErrorReportModel model = ErrorReportModel.builder()
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(exceptionModel)
            .service(SERVICE)
            .build();

        Response response = RequestFactory.createRequest()
            .body(model)
            .put(UrlFactory.create(serverPort, Endpoints.ADMIN_PANEL_INTERNAL_REPORT_ERROR));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        List<ErrorReport> reports = errorReportDao.findAll();
        assertThat(reports).hasSize(1);
        ErrorReport report = reports.get(0);
        assertThat(report.getId()).isNotNull();
        assertThat(report.getCreatedAt()).isNotNull();
        assertThat(report.getMessage()).isEqualTo(MESSAGE);
        assertThat(report.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(report.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(report.getService()).isEqualTo(SERVICE);
        assertThat(report.getException()).isEqualTo(exceptionModel);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderClient).sendMessage(eq(MessageGroup.ADMIN_PANEL_ERROR_REPORT), argumentCaptor.capture(), any());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.ADMIN_PANEL_ERROR_REPORT);
        assertThat(message.getRecipients()).isNull();
    }
}