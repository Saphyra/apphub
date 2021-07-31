package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataFriendApiClient;
import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkyXploreDataProxy {
    private final SkyXploreDataFriendApiClient dataFriendClient;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final AccessTokenProvider accessTokenProvider;
    private final LocaleProvider localeProvider;
    private final SkyXploreSavedGameClient savedGameClient;

    public List<FriendshipResponse> getFriends(AccessTokenHeader accessTokenHeader) {
        return dataFriendClient.getFriends(
            accessTokenHeaderConverter.convertDomain(accessTokenHeader),
            localeProvider.getLocaleValidated()
        );
    }

    public GameViewForLobbyCreation getGameForLobbyCreation(UUID gameId) {
        return savedGameClient.getGameForLobbyCreation(
            gameId,
            accessTokenProvider.getAsString(),
            localeProvider.getLocaleValidated()
        );
    }
}
