package com.github.saphyra.apphub.lib.error_handler;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.exception.RestException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
class ErrorHandlerAdvice {
    private static final String GENERAL_ERROR_CODE = "GENERAL_ERROR";

    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(FeignException.class)
    ResponseEntity<?> feignException(FeignException exception) {
        String content = exception.contentUTF8();
        HttpStatus status = HttpStatus.valueOf(exception.status());
        log.warn("Handling feignException with status {} and content {}", status, content);
        return new ResponseEntity<>(content, status);
    }

    @ExceptionHandler(RestException.class)
    ResponseEntity<ErrorResponse> restException(RestException exception) {
        log.warn("Error occurred with errorMessage {}:", exception.getErrorMessage(), exception);
        ErrorMessage errorMessage = exception.getErrorMessage();

        ErrorResponseWrapper errorResponse = errorResponseFactory.create(exception.getResponseStatus(), errorMessage.getErrorCode(), errorMessage.getParams());

        return new ResponseEntity<>(errorResponse.getErrorResponse(), errorResponse.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponse> generalException(RuntimeException exception) {
        log.error("Unknown exception occurred:", exception);
        ErrorResponseWrapper errorResponse = errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_CODE, new HashMap<>());

        return new ResponseEntity<>(errorResponse.getErrorResponse(), errorResponse.getStatus());
    }
}
