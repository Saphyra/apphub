package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.error_handler.service.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.error_handler.service.translation.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import feign.FeignException;
import feign.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerAdviceTest {
    private static final String CONTENT = "content";

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private ErrorHandlerAdvice underTest;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private FeignException feignException;

    @Mock
    private Request request;

    @Mock
    private ErrorResponseWrapper errorResponseWrapper;

    @Before
    public void setUp() {
        given(errorResponseWrapper.getErrorResponse()).willReturn(errorResponse);
        given(errorResponseWrapper.getStatus()).willReturn(HttpStatus.BAD_GATEWAY);
    }

    @Test
    public void feignException() {
        given(feignException.contentUTF8()).willReturn(CONTENT);
        given(feignException.status()).willReturn(400);
        given(feignException.request()).willReturn(request);

        ResponseEntity<?> result = underTest.feignException(feignException);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(CONTENT);
    }

    @Test
    public void notLoggedException() {
        given(errorResponseFactory.create(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(errorResponseWrapper);

        ResponseEntity<ErrorResponse> result = underTest.notLoggedException((NotLoggedException) ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "message"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }

    @Test
    public void loggedException() {
        given(errorResponseFactory.create(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(errorResponseWrapper);

        ResponseEntity<ErrorResponse> result = underTest.loggedException((LoggedException) ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "message"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }

    @Test
    public void reportedException() {
        given(errorResponseFactory.create(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(errorResponseWrapper);

        ReportedException exception = (ReportedException) ExceptionFactory.reportedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "message");
        ResponseEntity<ErrorResponse> result = underTest.reportedException(exception);

        verify(errorReporterService).report(HttpStatus.BAD_GATEWAY, errorResponse, exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }

    @Test
    public void generalException() {
        ErrorResponseWrapper wrapper = new ErrorResponseWrapper(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        given(errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(wrapper);
        RuntimeException exception = new RuntimeException();

        ResponseEntity<ErrorResponse> result = underTest.generalException(exception);

        verify(errorReporterService).report(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse, exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }
}