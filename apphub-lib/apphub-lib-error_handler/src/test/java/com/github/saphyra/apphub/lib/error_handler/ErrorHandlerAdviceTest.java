package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.error_handler.service.translation.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import feign.FeignException;
import feign.Request;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerAdviceTest {
    private static final String CONTENT = "content";
    private static final String MESSAGE = "message";
    private static final String PARAM_NAME = "paramName";

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

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private MethodParameter methodParameter;

    @Test
    public void httpMessageNotReadableException() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(MESSAGE, (HttpInputMessage) null);

        ResponseEntity<?> result = underTest.httpMessageNotReadableException(ex);

        verify(errorReporterService).report(ex.getMessage(), ex);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        ErrorResponse body = (ErrorResponse) result.getBody();
        assertThat(body.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(body.getParams()).isEqualTo(Map.of("error", ex.getMessage()));
    }

    @Test
    public void methodArgumentTypeMismatchException() {
        given(methodParameter.getParameterName()).willReturn(PARAM_NAME);
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(null, String.class, PARAM_NAME, methodParameter, new RuntimeException(MESSAGE));

        ResponseEntity<?> result = underTest.methodArgumentTypeMismatchException(ex);

        verify(errorReporterService).report(ex.getMessage(), ex);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        ErrorResponse body2 = (ErrorResponse) result.getBody();
        assertThat(body2.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(body2.getParams()).isEqualTo(Map.of(PARAM_NAME, ex.getMessage()));
    }

    @Test
    public void feignException() {
        given(feignException.contentUTF8()).willReturn(CONTENT);
        given(feignException.status()).willReturn(400);
        given(feignException.request()).willReturn(request);

        ResponseEntity<?> result = underTest.feignException(feignException);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(CONTENT);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void notLoggedException() {
        given(errorResponseWrapper.getErrorResponse()).willReturn(errorResponse);
        given(errorResponseWrapper.getStatus()).willReturn(HttpStatus.BAD_GATEWAY);
        given(errorResponseFactory.create(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(errorResponseWrapper);

        ResponseEntity<ErrorResponse> result = underTest.notLoggedException((NotLoggedException) ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "message"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(result.getBody()).isEqualTo(errorResponse);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void loggedException() {
        given(errorResponseWrapper.getErrorResponse()).willReturn(errorResponse);
        given(errorResponseWrapper.getStatus()).willReturn(HttpStatus.BAD_GATEWAY);
        given(errorResponseFactory.create(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(errorResponseWrapper);

        ResponseEntity<ErrorResponse> result = underTest.loggedException(ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "message"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(result.getBody()).isEqualTo(errorResponse);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void reportedException() {
        given(errorResponseWrapper.getErrorResponse()).willReturn(errorResponse);
        given(errorResponseWrapper.getStatus()).willReturn(HttpStatus.BAD_GATEWAY);
        given(errorResponseFactory.create(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(errorResponseWrapper);

        ReportedException exception = (ReportedException) ExceptionFactory.reportedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "message");
        ResponseEntity<ErrorResponse> result = underTest.reportedException(exception);

        verify(errorReporterService).report(HttpStatus.BAD_GATEWAY, errorResponse, exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(result.getBody()).isEqualTo(errorResponse);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void generalException() {
        ErrorResponseWrapper wrapper = new ErrorResponseWrapper(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        given(errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(wrapper);
        RuntimeException exception = new RuntimeException();

        ResponseEntity<ErrorResponse> result = underTest.generalException(servletRequest, exception);

        verify(errorReporterService).report(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse, exception);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(errorResponse);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }
}