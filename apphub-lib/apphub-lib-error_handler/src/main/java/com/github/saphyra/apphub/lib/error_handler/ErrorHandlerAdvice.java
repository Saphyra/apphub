package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.error_handler.service.translation.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import com.github.saphyra.apphub.lib.exception.RestException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
class ErrorHandlerAdvice {
    private final ErrorResponseFactory errorResponseFactory;
    private final ErrorReporterService errorReporterService;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.INVALID_PARAM)
            .params(Map.of("error", ex.getMessage()))
            .build();
        errorReporterService.report(ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, getHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.INVALID_PARAM)
            .params(Map.of(ex.getParameter().getParameterName(), ex.getMessage()))
            .build();
        errorReporterService.report(ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, getHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    ResponseEntity<?> feignException(FeignException exception) {
        String content = exception.contentUTF8();
        HttpStatus status = HttpStatus.valueOf(exception.status());
        log.warn("Handling feignException with status {} and content {} for {} - {}", status, content, exception.request().httpMethod(), exception.request().url());
        return new ResponseEntity<>(content, getHeaders(), status);
    }

    @ExceptionHandler(NotLoggedException.class)
    ResponseEntity<ErrorResponse> notLoggedException(NotLoggedException exception) {
        ErrorResponseWrapper errorResponse = getErrorResponse(exception);
        log.info("Returning errorResponse: {} with logMessage: {}", errorResponse, exception.getMessage());
        log.debug("Exception:", exception);

        return new ResponseEntity<>(errorResponse.getErrorResponse(), getHeaders(), errorResponse.getStatus());
    }

    @ExceptionHandler(LoggedException.class)
    ResponseEntity<ErrorResponse> loggedException(LoggedException exception) {
        ErrorResponseWrapper errorResponse = getErrorResponse(exception);
        log.warn("Returning errorResponse: {}", errorResponse, exception);

        return new ResponseEntity<>(errorResponse.getErrorResponse(), getHeaders(), errorResponse.getStatus());
    }

    @ExceptionHandler(ReportedException.class)
    ResponseEntity<ErrorResponse> reportedException(ReportedException exception) {
        ErrorResponseWrapper errorResponse = getErrorResponse(exception);
        log.warn("Returning errorResponse: {}", errorResponse, exception);

        errorReporterService.report(errorResponse.getStatus(), errorResponse.getErrorResponse(), exception);

        return new ResponseEntity<>(errorResponse.getErrorResponse(), getHeaders(), errorResponse.getStatus());
    }

    private ErrorResponseWrapper getErrorResponse(RestException exception) {
        ErrorMessage errorMessage = exception.getErrorMessage();
        return errorResponseFactory.create(exception.getResponseStatus(), errorMessage.getErrorCode(), errorMessage.getParams());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponse> generalException(HttpServletRequest request, RuntimeException exception) {
        log.error("Unknown exception occurred when calling {} - {}:", request.getMethod(), request.getRequestURI(), exception);
        ErrorResponseWrapper errorResponse = errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, new HashMap<>());

        errorReporterService.report(errorResponse.getStatus(), errorResponse.getErrorResponse(), exception);

        return new ResponseEntity<>(errorResponse.getErrorResponse(), getHeaders(), errorResponse.getStatus());
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
