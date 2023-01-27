package com.github.saphyra.apphub.lib.web_utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CookieUtilTest {
    private static final String COOKIE_NAME = "cookie_name";
    private static final String COOKIE_VALUE = "cookie_value";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private CookieUtil underTest;

    @Test
    void testGetCookieShouldReturnEmptyWhenNoCookies() {
        //GIVEN
        given(request.getCookies()).willReturn(null);
        //WHEN
        Optional<String> result = underTest.getCookie(request, COOKIE_NAME);
        //THEN
        verify(request).getCookies();

        assertThat(result).isEmpty();
    }

    @Test
    void testGetCookieShouldReturnEmptyWhenNotFound() {
        //GIVEN
        given(request.getCookies()).willReturn(new Cookie[0]);
        //WHEN
        Optional<String> result = underTest.getCookie(request, COOKIE_NAME);
        //THEN
        verify(request).getCookies();
        assertThat(result).isEmpty();
    }

    @Test
    void testGetCookieShouldReturnCookie() {
        //GIVEN
        Cookie cookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);
        Cookie cookie2 = new Cookie("asd", "das");
        given(request.getCookies()).willReturn(new Cookie[]{cookie, cookie2});
        //WHEN
        Optional<String> result = underTest.getCookie(request, COOKIE_NAME);
        //THEN
        assertThat(result).contains(COOKIE_VALUE);
        verify(request).getCookies();
    }

    @Test
    void testSetCookie() {
        //WHEN
        underTest.setCookie(response, COOKIE_NAME, COOKIE_VALUE);
        //THEN
        ArgumentCaptor<Cookie> argumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getValue()).isEqualTo(COOKIE_VALUE);
        assertThat(argumentCaptor.getValue().getName()).isEqualTo(COOKIE_NAME);
    }
}