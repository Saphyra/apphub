package com.github.saphyra.apphub.service.platform.main_gateway.util;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import static com.github.saphyra.apphub.lib.common_domain.Constants.REQUEST_TYPE_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class UriUtils {
    private final AntPathMatcher antPathMatcher;

    public boolean isWebPath(String requestUri) {
        return antPathMatcher.match(Constants.WEB_PATH_PATTERN, requestUri);
    }

    public boolean isResourcePath(String requestUri) {
        return antPathMatcher.match(Constants.RESOURCE_PATH_PATTERN, requestUri);
    }

    public boolean isRestCall(HttpHeaders httpHeaders) {
        return Constants.REQUEST_TYPE_VALUE.equals(httpHeaders.getFirst(REQUEST_TYPE_HEADER));
    }
}
