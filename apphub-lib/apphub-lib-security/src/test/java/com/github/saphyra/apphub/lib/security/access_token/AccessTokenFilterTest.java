package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccessTokenFilterTest {
    private static final String ACCESS_TOKEN_HEADER = "access-token-header";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @InjectMocks
    private AccessTokenFilter underTest;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void accessTokenHeaderNotSet() throws ServletException, IOException {
        given(request.getHeader(Constants.ACCESS_TOKEN_HEADER)).willReturn(null);

        underTest.doFilterInternal(request, response, filterChain);

        verify(accessTokenProvider, times(0)).set(any());
        verify(filterChain).doFilter(request, response);
        verify(accessTokenProvider).clear();
    }

    @Test
    public void accessTokenHeaderBlank() throws ServletException, IOException {
        given(request.getHeader(Constants.ACCESS_TOKEN_HEADER)).willReturn(" ");

        underTest.doFilterInternal(request, response, filterChain);

        verify(accessTokenProvider, times(0)).set(any());
        verify(filterChain).doFilter(request, response);
        verify(accessTokenProvider).clear();
    }

    @Test
    public void accessTokenHeaderIsSet() throws ServletException, IOException {
        given(request.getHeader(Constants.ACCESS_TOKEN_HEADER)).willReturn(ACCESS_TOKEN_HEADER);
        given(accessTokenHeaderConverter.convert(ACCESS_TOKEN_HEADER)).willReturn(accessTokenHeader);

        underTest.doFilterInternal(request, response, filterChain);

        verify(accessTokenProvider).set(accessTokenHeader);
        verify(filterChain).doFilter(request, response);
        verify(accessTokenProvider).clear();
    }

    @Test
    public void filterThrowException() throws ServletException, IOException {
        given(request.getHeader(Constants.ACCESS_TOKEN_HEADER)).willReturn(ACCESS_TOKEN_HEADER);
        given(accessTokenHeaderConverter.convert(ACCESS_TOKEN_HEADER)).willReturn(accessTokenHeader);
        RuntimeException thrownException = new RuntimeException();
        doThrow(thrownException).when(filterChain).doFilter(request, response);

        Throwable ex = catchThrowable(() -> underTest.doFilterInternal(request, response, filterChain));

        assertThat(ex).isEqualTo(thrownException);

        verify(accessTokenProvider).set(accessTokenHeader);
        verify(filterChain).doFilter(request, response);
        verify(accessTokenProvider).clear();
    }
}