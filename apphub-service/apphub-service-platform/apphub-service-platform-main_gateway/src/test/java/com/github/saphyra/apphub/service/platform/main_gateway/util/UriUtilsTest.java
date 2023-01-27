package com.github.saphyra.apphub.service.platform.main_gateway.util;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UriUtilsTest {
    private static final String REQUEST_URI = "request-uri";

    @Mock
    private AntPathMatcher antPathMatcher;

    @InjectMocks
    private UriUtils underTest;

    @Mock
    private HttpHeaders httpHeaders;

    @Test
    public void isWebPath() {
        given(antPathMatcher.match(Constants.WEB_PATH_PATTERN, REQUEST_URI)).willReturn(true);

        assertThat(underTest.isWebPath(REQUEST_URI)).isTrue();
    }

    @Test
    public void isResourcePath() {
        given(antPathMatcher.match(Constants.RESOURCE_PATH_PATTERN, REQUEST_URI)).willReturn(true);

        assertThat(underTest.isResourcePath(REQUEST_URI)).isTrue();
    }

    @Test
    public void isRestCall() {
        given(httpHeaders.getFirst(Constants.REQUEST_TYPE_HEADER)).willReturn(Constants.REQUEST_TYPE_VALUE);

        assertThat(underTest.isRestCall(httpHeaders)).isTrue();
    }
}