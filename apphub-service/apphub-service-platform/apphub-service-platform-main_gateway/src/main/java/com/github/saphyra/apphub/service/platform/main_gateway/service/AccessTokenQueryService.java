package com.github.saphyra.apphub.service.platform.main_gateway.service;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true) //TODO delete
public class AccessTokenQueryService {
    public Optional<AccessToken> getAccessToken(String accessTokenId) {
        return Optional.empty();
    }
}
