package com.github.saphyra.apphub.lib.common_util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Component
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
