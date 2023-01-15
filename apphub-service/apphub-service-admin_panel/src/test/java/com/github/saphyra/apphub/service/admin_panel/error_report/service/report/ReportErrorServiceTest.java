package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.overview.ErrorReportToOverviewConverter;
import com.github.saphyra.apphub.service.admin_panel.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.admin_panel.ws.WebSocketMessageFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReportErrorServiceTest {
    private static final String MESSAGE = "message";
    private static final String SERVICE = "service";

    @Mock
    private ErrorReportFactory errorReportFactory;

    @Mock
    private ErrorReportDao errorReportDao;

    @Mock
    private WebSocketMessageFactory webSocketMessageFactory;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private ErrorReportToOverviewConverter converter;

    @InjectMocks
    private ReportErrorService underTest;

    @Mock
    private ErrorReport errorReport;

    @Mock
    private WebSocketMessage webSocketMessage;

    @Mock
    private ErrorReportOverview errorReportOverview;

    @Test
    public void notNullModelId() {
        ErrorReportModel model = ErrorReportModel.builder()
            .id(UUID.randomUUID())
            .message(MESSAGE)
            .service(SERVICE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.saveReport(model));

        ExceptionValidator.validateInvalidParam(ex, "id", "must be null");
    }

    @Test
    public void blankMessage() {
        ErrorReportModel model = ErrorReportModel.builder()
            .message(" ")
            .service(SERVICE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.saveReport(model));

        ExceptionValidator.validateInvalidParam(ex, "message", "must not be null or blank");
    }

    @Test
    public void blankService() {
        ErrorReportModel model = ErrorReportModel.builder()
            .service(" ")
            .message(MESSAGE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.saveReport(model));

        ExceptionValidator.validateInvalidParam(ex, "service", "must not be null or blank");
    }

    @Test
    public void saveReport() {
        ErrorReportModel model = ErrorReportModel.builder()
            .message(MESSAGE)
            .service(SERVICE)
            .build();

        given(webSocketMessageFactory.create((List<UUID>) null, WebSocketEventName.ADMIN_PANEL_ERROR_REPORT, errorReportOverview)).willReturn(webSocketMessage);
        given(converter.convert(errorReport)).willReturn(errorReportOverview);

        given(errorReportFactory.create(model)).willReturn(errorReport);

        underTest.saveReport(model);

        verify(errorReportDao).save(errorReport);
        verify(messageSenderProxy).sendToErrorReport(webSocketMessage);
    }
}