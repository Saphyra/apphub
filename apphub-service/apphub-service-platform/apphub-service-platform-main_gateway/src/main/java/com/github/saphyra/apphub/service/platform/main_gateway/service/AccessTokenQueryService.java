package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.api.user.model.login.InternalAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenQueryService {
    private final AccessTokenIdConverter accessTokenIdConverter;
    private final AccessTokenCache accessTokenCache;

    public Optional<InternalAccessTokenResponse> getAccessToken(String accessTokenId) {
        return accessTokenIdConverter.convertAccessTokenId(accessTokenId)
            .flatMap(this::getAccessToken);
    }

    private Optional<InternalAccessTokenResponse> getAccessToken(UUID accessTokenId) {
        try {
            return accessTokenCache.get(accessTokenId);
        } catch (Exception e) {
            log.debug("Failed to query accessToken by accessTokenId {}: {}", accessTokenId, e.getMessage());
            return Optional.empty();
        }
    }
}
