package com.github.saphyra.apphub.service.platform.message_sender.session;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.RestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
class WsSessionAccessTokenProvider {
    private final UuidConverter uuidConverter;

    UUID getAccessTokenId(Map<String, String> cookies) {
        return Optional.ofNullable(cookies.get(Constants.ACCESS_TOKEN_COOKIE))
            .map(uuidConverter::convertEntity)
            .orElseThrow(() -> RestException.createNonTranslated(HttpStatus.BAD_REQUEST, "AccessToken cookie not found."));
    }
}
