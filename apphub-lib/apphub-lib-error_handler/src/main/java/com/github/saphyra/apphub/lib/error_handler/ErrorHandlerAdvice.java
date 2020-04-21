package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorResponse;
import com.github.saphyra.apphub.lib.error_handler.exception.RestException;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
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
public class ErrorHandlerAdvice {
    public static final String GENERAL_ERROR_CODE = "GENERAL_ERROR";

    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(RestException.class)
    ResponseEntity<ErrorResponse> restException(RestException exception) {
        log.warn("Error occurred with errorMessage {}:", exception.getErrorMessage(), exception);
        ErrorMessage errorMessage = exception.getErrorMessage();

        ErrorResponse errorResponse = errorResponseFactory.create(exception.getResponseStatus(), errorMessage.getErrorCode(), errorMessage.getParams());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getHttpStatus()));
    }

    @ExceptionHandler(RuntimeException.class)
        //TODO unit test
    ResponseEntity<ErrorResponse> generalException(RuntimeException exception) {
        log.error("Unknown exception occurred:", exception);
        ErrorResponse errorResponse = errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_CODE, new HashMap<>());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getHttpStatus()));
    }
}
