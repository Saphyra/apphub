package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.api.user.authentication.client.UserAuthenticationApiClient;
import com.github.saphyra.apphub.api.user.authentication.model.response.InternalAccessTokenResponse;
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
    private final UserAuthenticationApiClient authenticationApi;

    public Optional<InternalAccessTokenResponse> getAccessToken(String accessTokenId) {
        return accessTokenIdConverter.convertAccessTokenId(accessTokenId)
            .flatMap(this::getAccessToken);
    }

    private Optional<InternalAccessTokenResponse> getAccessToken(UUID accessTokenId) {
        try {
            return Optional.of(authenticationApi.getAccessTokenById(accessTokenId));
        } catch (Exception e) {
            log.warn("Failed to query accessToken by accessTokenId {}: {}", accessTokenId, e.getMessage());
            return Optional.empty();
        }
    }
}