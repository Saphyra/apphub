package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.lib.error_handler.exception.RestException;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
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

        given(errorResponseFactory.create(HttpStatus.BAD_REQUEST, ERROR_CODE, params)).willReturn(errorResponse);
        given(errorResponse.getHttpStatus()).willReturn(HttpStatus.UNAUTHORIZED.value());

        ResponseEntity<ErrorResponse> result = underTest.restException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }

    @Test
    public void generalException() {
        given(errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, "GENERAL_ERROR", new HashMap<>())).willReturn(errorResponse);
        given(errorResponse.getHttpStatus()).willReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());

        ResponseEntity<ErrorResponse> result = underTest.generalException(new RuntimeException());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }
}