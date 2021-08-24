package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReportErrorServiceTest {
    private static final String MESSAGE = "message";
    private static final String SERVICE = "service";

    @Mock
    private ErrorReportFactory errorReportFactory;

    @Mock
    private ErrorReportDao errorReportDao;

    @InjectMocks
    private ReportErrorService underTest;

    @Mock
    private ErrorReport errorReport;

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

        given(errorReportFactory.create(model)).willReturn(errorReport);

        underTest.saveReport(model);

        verify(errorReportDao).save(errorReport);
    }
}