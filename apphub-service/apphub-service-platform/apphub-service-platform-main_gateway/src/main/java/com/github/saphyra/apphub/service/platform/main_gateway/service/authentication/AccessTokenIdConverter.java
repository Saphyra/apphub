package com.github.saphyra.apphub.service.platform.main_gateway.service.authentication;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class AccessTokenIdConverter {
    private final UuidConverter uuidConverter;

    Optional<UUID> convertAccessTokenId(String accessTokenIdString) {
        try {
            return uuidConverter.convertEntity(Optional.of(accessTokenIdString));
        } catch (Exception ex) {
            log.warn("Failed to parse accessToken: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
