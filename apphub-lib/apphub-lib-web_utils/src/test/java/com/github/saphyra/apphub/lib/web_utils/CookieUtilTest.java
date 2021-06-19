package com.github.saphyra.apphub.lib.web_utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CookieUtilTest {
    private static final String COOKIE_NAME = "cookie_name";
    private static final String COOKIE_VALUE = "cookie_value";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private CookieUtil cookieUtil;

    @Test
    public void testGetCookieShouldReturnEmptyWhenNoCookies() {
        //GIVEN
        when(request.getCookies()).thenReturn(null);
        //WHEN
        Optional<String> result = cookieUtil.getCookie(request, COOKIE_NAME);
        //THEN
        verify(request).getCookies();
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetCookieShouldReturnEmptyWhenNotFound() {
        //GIVEN
        when(request.getCookies()).thenReturn(new Cookie[0]);
        //WHEN
        Optional<String> result = cookieUtil.getCookie(request, COOKIE_NAME);
        //THEN
        verify(request).getCookies();
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetCookieShouldReturnCookie() {
        //GIVEN
        Cookie cookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);
        Cookie cookie2 = new Cookie("asd", "das");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie, cookie2});
        //WHEN
        Optional<String> result = cookieUtil.getCookie(request, COOKIE_NAME);
        //THEN
        assertTrue(result.isPresent());
        verify(request).getCookies();
        assertEquals(COOKIE_VALUE, result.get());
    }

    @Test
    public void testSetCookie() {
        //WHEN
        cookieUtil.setCookie(response, COOKIE_NAME, COOKIE_VALUE);
        //THEN
        ArgumentCaptor<Cookie> argumentCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(argumentCaptor.capture());
        assertEquals(COOKIE_VALUE, argumentCaptor.getValue().getValue());
        assertEquals(COOKIE_NAME, argumentCaptor.getValue().getName());
    }

}