package com.github.saphyra.apphub.service.admin_panel.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.StackTraceModel;
import com.github.saphyra.apphub.api.platform.web_content.client.LocalizationClient;
import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.ErrorResponseValidator;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import com.github.saphyra.apphub.test.web_socket.ApphubWsClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.test.common.TestConstants.DEFAULT_LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class ErrorReporterControllerImplIt_reportErrorTest {
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
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationClient localizationClient;

    @MockBean
    private UserAuthenticationClient userAuthenticationClient;

    @Autowired
    private ErrorReportDao errorReportDao;

    @Autowired
    private CommonConfigProperties commonConfigProperties;

    @Autowired
    private ObjectMapperWrapper objectMapperWrapper;

    @BeforeEach
    public void setUp() {
        given(localizationClient.translate(anyString(), eq(DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @AfterEach
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
    public void saveReport() throws URISyntaxException {
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

        given(userAuthenticationClient.getAccessTokenById(ACCESS_TOKEN_ID, commonConfigProperties.getDefaultLocale())).willReturn(InternalAccessTokenResponse.builder().userId(USER_ID).build());
        ApphubWsClient apphubWsClient = new ApphubWsClient(serverPort, Endpoints.WS_CONNECTION_ADMIN_PANEL_ERROR_REPORT, ACCESS_TOKEN_ID, commonConfigProperties.getDefaultLocale());

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

        WebSocketEvent event = apphubWsClient.awaitForEvent(WebSocketEventName.ADMIN_PANEL_ERROR_REPORT)
            .orElseThrow(() -> new RuntimeException("WebSocket event not arrived."));

        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.ADMIN_PANEL_ERROR_REPORT);
        ErrorReportOverview errorReportOverview = objectMapperWrapper.convertValue(event.getPayload(), ErrorReportOverview.class);

        assertThat(errorReportOverview.getId()).isEqualTo(report.getId());
        assertThat(errorReportOverview.getCreatedAt()).isNotNull();
        assertThat(errorReportOverview.getResponseStatus()).isEqualTo(report.getResponseStatus());
        assertThat(errorReportOverview.getMessage()).isEqualTo(report.getMessage());
        assertThat(errorReportOverview.getStatus()).isEqualTo(report.getStatus().name());
        assertThat(errorReportOverview.getService()).isEqualTo(report.getService());
    }
}