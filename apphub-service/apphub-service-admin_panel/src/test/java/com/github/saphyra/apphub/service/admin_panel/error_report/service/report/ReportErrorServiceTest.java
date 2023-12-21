package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.overview.ErrorReportToOverviewConverter;
import com.github.saphyra.apphub.service.admin_panel.ws.ErrorReportWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReportErrorServiceTest {
    private static final String MESSAGE = "message";
    private static final String SERVICE = "service";

    @Mock
    private ErrorReportDtoFactory errorReportDtoFactory;

    @Mock
    private ErrorReportDao errorReportDao;

    @Mock
    private ErrorReportWebSocketHandler errorReportWebSocketHandler;

    @Mock
    private ErrorReportToOverviewConverter converter;

    @InjectMocks
    private ReportErrorService underTest;

    @Mock
    private ErrorReportDto errorReport;

    @Mock
    private ErrorReportOverview errorReportOverview;

    @Test
    public void notNullModelId() {
        ErrorReport model = ErrorReport.builder()
            .id(UUID.randomUUID())
            .message(MESSAGE)
            .service(SERVICE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.saveReport(model));

        ExceptionValidator.validateInvalidParam(ex, "id", "must be null");
    }

    @Test
    public void blankMessage() {
        ErrorReport model = ErrorReport.builder()
            .message(" ")
            .service(SERVICE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.saveReport(model));

        ExceptionValidator.validateInvalidParam(ex, "message", "must not be null or blank");
    }

    @Test
    public void blankService() {
        ErrorReport model = ErrorReport.builder()
            .service(" ")
            .message(MESSAGE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.saveReport(model));

        ExceptionValidator.validateInvalidParam(ex, "service", "must not be null or blank");
    }

    @Test
    public void saveReport() {
        ErrorReport model = ErrorReport.builder()
            .message(MESSAGE)
            .service(SERVICE)
            .build();

        given(converter.convert(errorReport)).willReturn(errorReportOverview);

        given(errorReportDtoFactory.create(model)).willReturn(errorReport);

        underTest.saveReport(model);

        verify(errorReportDao).save(errorReport);
        ArgumentCaptor<WebSocketEvent> argumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(errorReportWebSocketHandler).should().sendToAllConnectedClient(argumentCaptor.capture());
        WebSocketEvent event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.ADMIN_PANEL_ERROR_REPORT);
        assertThat(event.getPayload()).isEqualTo(errorReportOverview);
    }
}