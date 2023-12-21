package com.github.saphyra.apphub.service.admin_panel.error_report.service.details;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ErrorReportDetailsQueryServiceTest {
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private ErrorReportDao errorReportDao;

    @Mock
    private ErrorReportToResponseConverter converter;

    @InjectMocks
    private ErrorReportDetailsQueryService underTest;

    @Mock
    private ErrorReportDto errorReport;

    @Mock
    private ErrorReport model;

    @Test
    public void findById_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findById(ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void findById_unread() {
        given(errorReportDao.findById(ID)).willReturn(Optional.of(errorReport));
        given(converter.convert(errorReport)).willReturn(model);
        given(errorReport.getStatus()).willReturn(ErrorReportStatus.UNREAD);

        ErrorReport result = underTest.findById(ID);

        assertThat(result).isEqualTo(model);
        verify(errorReport).setStatus(ErrorReportStatus.READ);
        verify(errorReportDao).save(errorReport);
    }

    @Test
    public void findById_read() {
        given(errorReportDao.findById(ID)).willReturn(Optional.of(errorReport));
        given(converter.convert(errorReport)).willReturn(model);
        given(errorReport.getStatus()).willReturn(ErrorReportStatus.READ);

        ErrorReport result = underTest.findById(ID);

        assertThat(result).isEqualTo(model);
        verify(errorReport, times(0)).setStatus(any());
        verify(errorReportDao, times(0)).save(errorReport);
    }
}