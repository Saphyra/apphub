package com.github.saphyra.apphub.lib.web_utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class RequestContextProvider {
    public HttpServletRequest getCurrentHttpRequest() {
        return getHttpServletRequestOptional()
            .orElseThrow(() -> new RuntimeException("RequestContext is not available from the current thread."));
    }

    public Optional<HttpServletRequest> getHttpServletRequestOptional() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
            .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
            .map(ServletRequestAttributes::getRequest);
    }
}
