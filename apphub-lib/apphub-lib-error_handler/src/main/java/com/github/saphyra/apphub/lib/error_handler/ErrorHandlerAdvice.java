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

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
class ErrorHandlerAdvice {
    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(RestException.class)
    ResponseEntity<ErrorResponse> restException(RestException exception) {
        log.warn("Error occurred with errorMessage {}:", exception.getErrorMessage(), exception);
        ErrorMessage errorMessage = exception.getErrorMessage();

        ErrorResponse errorResponse = errorResponseFactory.create(exception.getResponseStatus(), errorMessage.getErrorCode(), errorMessage.getParams());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getHttpStatus()));
    }
}
