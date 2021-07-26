package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GetErrorReportsRequestValidatorTest {
    private static final String START_TIME = "1996-02-15 23:48:58.134";
    private static final String END_TIME = "2016-01-16";
    private static final Integer PAGE_SIZE = 45;
    private static final Integer PAGE = 435;

    @InjectMocks
    private GetErrorReportsRequestValidator underTest;

    @Mock
    private GetErrorReportsRequest request;

    @Before
    public void setUp() {
        given(request.getStatusCode()).willReturn(HttpStatus.BAD_GATEWAY.value());
        given(request.getStartTime()).willReturn(START_TIME);
        given(request.getEndTime()).willReturn(END_TIME);
        given(request.getPageSize()).willReturn(PAGE_SIZE);
        given(request.getStatus()).willReturn(ErrorReportStatus.MARKED.name());
        given(request.getPage()).willReturn(PAGE);
    }

    @Test
    public void invalidStatusCode() {
        given(request.getStatusCode()).willReturn(1);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "statusCode", "invalid value");
    }

    @Test
    public void invalidStartDate() {
        given(request.getStartTime()).willReturn("Józsi");

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "startTime", "invalid format");
    }

    @Test
    public void invalidEndDate() {
        given(request.getEndTime()).willReturn("Józsi");

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "endTime", "invalid format");
    }

    @Test
    public void invalidStatus() {
        given(request.getStatus()).willReturn("Józsi");

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "status", "invalid value");
    }

    @Test
    public void nullPageSize() {
        given(request.getPageSize()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "pageSize", "must not be null");
    }

    @Test
    public void tooLowPageSize() {
        given(request.getPageSize()).willReturn(0);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "pageSize", "too low");
    }

    @Test
    public void nullPage() {
        given(request.getPage()).willReturn(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "page", "must not be null");
    }

    @Test
    public void tooLowPage() {
        given(request.getPage()).willReturn(0);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "page", "too low");
    }

    @Test
    public void valid() {
        System.out.println(LocalDateTime.now());
        underTest.validate(request);
    }

    @Test
    public void validWithMandatoryFieldsOnly() {
        given(request.getStatusCode()).willReturn(null);
        given(request.getStartTime()).willReturn(null);
        given(request.getEndTime()).willReturn(null);
        given(request.getPageSize()).willReturn(PAGE_SIZE);
        given(request.getStatus()).willReturn(ErrorReportStatus.MARKED.name());

        underTest.validate(request);
    }
}