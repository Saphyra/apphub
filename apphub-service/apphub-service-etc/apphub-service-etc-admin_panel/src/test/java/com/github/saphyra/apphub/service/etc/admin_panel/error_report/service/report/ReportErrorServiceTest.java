package com.github.saphyra.apphub.service.etc.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.ErrorReport;
import com.github.saphyra.apphub.api.etc.admin_panel.model.model.error_report.ErrorReportOverview;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.etc.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.etc.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.etc.admin_panel.error_report.service.overview.ErrorReportToOverviewConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
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

        ExceptionValidator.validateInvalidParam(ex, "message", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    public void blankService() {
        ErrorReport model = ErrorReport.builder()
            .service(" ")
            .message(MESSAGE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.saveReport(model));

        ExceptionValidator.validateInvalidParam(ex, "service", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
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
    }
}