package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WsSessionCookieParserTest {
    @InjectMocks
    private WsSessionCookieParser underTest;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders httpHeaders;

    @Before
    public void setUp() {
        given(request.getHeaders()).willReturn(httpHeaders);
    }

    @Test(expected = LoggedException.class)
    public void cookieListNotFound() {
        given(httpHeaders.get(Constants.COOKIE_HEADER)).willReturn(null);

        underTest.getCookies(request);
    }

    @Test(expected = LoggedException.class)
    public void cookieListEmpty() {
        given(httpHeaders.get(Constants.COOKIE_HEADER)).willReturn(Collections.emptyList());

        underTest.getCookies(request);
    }

    @Test
    public void getCookies() {
        given(httpHeaders.get(Constants.COOKIE_HEADER)).willReturn(Arrays.asList("key1=value1; key2=value2"));

        Map<String, String> result = underTest.getCookies(request);

        assertThat(result)
            .containsEntry("key1", "value1")
            .containsEntry("key2", "value2");
    }
}