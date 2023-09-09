package com.github.saphyra.apphub.lib.web_socket.core.handshake;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
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
//TODO unit test
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
            throw ExceptionFactory.loggedException(HttpStatus.BAD_REQUEST, ErrorCode.UNKNOWN_ERROR, "Cookies not found in headers");
        }

        if (cookieList.isEmpty()) {
            throw ExceptionFactory.loggedException(HttpStatus.BAD_REQUEST, ErrorCode.UNKNOWN_ERROR, "Cookie list is empty.");
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
