package com.github.saphyra.apphub.service.skyxplore.data.common;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameApiClient;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameProxy {
    private final SkyXploreGameApiClient gameClient;
    private final AccessTokenProvider accessTokenProvider;
    private final LocaleProvider localeProvider;

    public Optional<UUID> getGameId() {
        OneParamResponse<UUID> response = gameClient.getGameId(accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
        return Optional.ofNullable(response.getValue());
    }

    public UUID getGameIdValidated(UUID userId) {
        return getGameId()
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(userId + " is not in a game."));
    }
}
