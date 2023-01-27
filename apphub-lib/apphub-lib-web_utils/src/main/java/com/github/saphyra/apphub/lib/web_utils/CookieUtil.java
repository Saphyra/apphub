package com.github.saphyra.apphub.lib.web_utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {
    public Optional<String> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookieArray = request.getCookies();
        if (cookieArray == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookieArray)
            .filter(c -> c.getName().equals(name))
            .findAny()
            .map(Cookie::getValue);
    }

    public void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, -1);
    }

    public void setCookie(HttpServletResponse response, String name, String value, int expiration) {
        response.addCookie(createCookie(name, value, expiration));
    }

    private Cookie createCookie(String name, String value, int expiration) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiration);
        return cookie;
    }
}
