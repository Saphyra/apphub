package com.github.saphyra.apphub.lib.error_handler;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponseWrapper;
import com.github.saphyra.apphub.lib.error_handler.service.ErrorResponseFactory;
import feign.FeignException;
import feign.Request;
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

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerAdviceTest {
    private static final String CONTENT = "content";

    @Mock
    private ErrorResponseFactory errorResponseFactory;

    @InjectMocks
    private ErrorHandlerAdvice underTest;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private FeignException feignException;

    @Mock
    private Request request;

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
    public void generalException() {
        ErrorResponseWrapper wrapper = new ErrorResponseWrapper(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        given(errorResponseFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, new HashMap<>())).willReturn(wrapper);

        ResponseEntity<ErrorResponse> result = underTest.generalException(new RuntimeException());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(errorResponse);
    }
}