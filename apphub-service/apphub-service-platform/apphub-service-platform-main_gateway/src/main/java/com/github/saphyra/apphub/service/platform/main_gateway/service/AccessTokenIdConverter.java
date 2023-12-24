package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenIdConverter {
    private final UuidConverter uuidConverter;

    public Optional<UUID> convertAccessTokenId(String accessTokenIdString) {
        try {
            return uuidConverter.convertEntity(Optional.ofNullable(accessTokenIdString));
        } catch (Exception ex) {
            log.warn("Failed to parse accessToken: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
