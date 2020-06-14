package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.RestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerAdviceTest {
    private static final String ERROR_CODE = "error-code";

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @InjectMocks
    private ErrorHandlerAdvice underTest;

    @Mock
    private Map<String, String> params;

    @Mock
    private ErrorResponse errorResponse;

    @Test
    public void restException() {
        ErrorMessage errorMessage = new ErrorMessage(ERROR_CODE, params);
        RestException exception = new BadRequestException(errorMessage, "");
        ErrorResponseWrapper wrapper = new ErrorResponseWrapper(errorResponse, HttpStatus.UNAUTHORIZED);

        given(errorResponseFactory.create(HttpStatus.BAD_REQUEST, ERROR_CODE, params)).willReturn(wrapper);

        ResponseEntity<ErrorResponse> result = underTest.restException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }

    @Test
    public void generalException() {
        ErrorResponseWrapper wrapper = new ErrorResponseWrapper(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        given(errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, "GENERAL_ERROR", new HashMap<>())).willReturn(wrapper);

        ResponseEntity<ErrorResponse> result = underTest.generalException(new RuntimeException());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }
}