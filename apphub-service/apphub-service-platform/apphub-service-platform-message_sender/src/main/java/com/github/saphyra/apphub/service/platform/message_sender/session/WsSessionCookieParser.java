package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.exception.RestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
@Slf4j
class WsSessionCookieParser {
    Map<String, String> getCookies(ServerHttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        if (log.isDebugEnabled()) {
            log.debug("HttpHeaders: {}", httpHeaders);
        }

        List<String> cookieList = httpHeaders.get(Constants.COOKIE_HEADER);

        if (log.isDebugEnabled()) {
            log.debug("Cookies: {}", cookieList);
        }

        if (isNull(cookieList)) {
            throw RestException.createNonTranslated(HttpStatus.BAD_REQUEST, "Cookies not found in headers");
        }

        if (cookieList.isEmpty()) {
            throw RestException.createNonTranslated(HttpStatus.BAD_REQUEST, "Cookie list it empty.");
        }

        String cookieString = cookieList.get(0);
        String[] cookieArray = cookieString.split("; ");

        return Arrays.stream(cookieArray)
            .map(s -> {
                String[] sArray = s.split("=");
                return BiWrapper.<String, String>builder()
                    .entity1(sArray[0])
                    .entity2(sArray[1])
                    .build();
            })
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));
    }
}
