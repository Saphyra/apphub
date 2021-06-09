package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import com.github.saphyra.apphub.lib.exception.RestException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
class ErrorHandlerAdvice {
    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(FeignException.class)
    ResponseEntity<?> feignException(FeignException exception) {
        String content = exception.contentUTF8();
        HttpStatus status = HttpStatus.valueOf(exception.status());
        log.warn("Handling feignException with status {} and content {} for {} - {}", status, content, exception.request().httpMethod(), exception.request().url());
        return new ResponseEntity<>(content, status);
    }

    @ExceptionHandler(NotLoggedException.class)
        //TODO unit test
    ResponseEntity<ErrorResponse> expectedException(NotLoggedException exception) {
        ErrorResponseWrapper errorResponse = getErrorResponse(exception);
        log.info("Returning errorResponse: {} with logMessage: {}", errorResponse, exception.getErrorMessage());
        log.debug("Exception:", exception);

        return new ResponseEntity<>(errorResponse.getErrorResponse(), errorResponse.getStatus());
    }

    @ExceptionHandler(LoggedException.class)
        //TODO unit test
    ResponseEntity<ErrorResponse> loggedException(LoggedException exception) {
        ErrorResponseWrapper errorResponse = getErrorResponse(exception);
        log.info("Returning errorResponse: {}", errorResponse, exception);

        return new ResponseEntity<>(errorResponse.getErrorResponse(), errorResponse.getStatus());
    }

    @ExceptionHandler(ReportedException.class)
        //TODO unit test
    ResponseEntity<ErrorResponse> reportedException(ReportedException exception) {
        ErrorResponseWrapper errorResponse = getErrorResponse(exception);
        log.info("Returning errorResponse: {}", errorResponse, exception);

        //TODO report

        return new ResponseEntity<>(errorResponse.getErrorResponse(), errorResponse.getStatus());
    }

    private ErrorResponseWrapper getErrorResponse(RestException exception) {
        ErrorMessage errorMessage = exception.getErrorMessage();
        return errorResponseFactory.create(exception.getResponseStatus(), errorMessage.getErrorCode(), errorMessage.getParams());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponse> generalException(RuntimeException exception) {
        log.error("Unknown exception occurred:", exception);
        ErrorResponseWrapper errorResponse = errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, new HashMap<>());
        //TODO report

        return new ResponseEntity<>(errorResponse.getErrorResponse(), errorResponse.getStatus());
    }
}
