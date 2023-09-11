package com.github.saphyra.apphub.lib.web_socket.core.handshake;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class WsSessionUserIdProvider {
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;

    Optional<UUID> findUserId(ServerHttpRequest request) {
        return request.getHeaders()
            .getOrEmpty(Constants.ACCESS_TOKEN_HEADER)
            .stream()
            .findFirst()
            .map(accessTokenHeaderConverter::convertEntity)
            .map(AccessTokenHeader::getUserId);
    }
}
