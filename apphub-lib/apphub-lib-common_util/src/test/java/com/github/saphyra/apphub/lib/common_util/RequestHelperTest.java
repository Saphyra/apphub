package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RequestHelperTest {
    private static final String ERROR_RESPONSE = "error-response";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private RequestHelper underTest;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private PrintWriter printWriter;

    @Test
    public void isRestCall() {
        given(request.getHeader(Constants.REQUEST_TYPE_HEADER)).willReturn(Constants.REQUEST_TYPE_VALUE);

        boolean result = underTest.isRestCall(request);

        assertThat(result).isTrue();
    }

    @Test
    public void isRestCall_unknownValue() {
        given(request.getHeader(Constants.REQUEST_TYPE_HEADER)).willReturn("asfa");

        boolean result = underTest.isRestCall(request);

        assertThat(result).isFalse();
    }

    @Test
    public void isRestCall_headerNotPresent() {
        given(request.getHeader(Constants.REQUEST_TYPE_HEADER)).willReturn(null);

        boolean result = underTest.isRestCall(request);

        assertThat(result).isFalse();
    }

    @Test
    public void sendRestError() throws IOException {
        given(objectMapperWrapper.writeValueAsString(errorResponse)).willReturn(ERROR_RESPONSE);
        given(response.getWriter()).willReturn(printWriter);

        underTest.sendRestError(response, HttpStatus.BAD_REQUEST, errorResponse);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(printWriter).write(ERROR_RESPONSE);
        verify(printWriter).flush();
        verify(printWriter).close();
    }
}