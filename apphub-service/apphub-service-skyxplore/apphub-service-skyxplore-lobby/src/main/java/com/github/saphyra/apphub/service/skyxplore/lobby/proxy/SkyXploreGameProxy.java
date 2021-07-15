package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkyXploreGameProxy {
    private final SkyXploreGameCreationApiClient gameCreationClient;
    private final LocaleProvider localeProvider;

    public void loadGame(SkyXploreLoadGameRequest request) {
        gameCreationClient.loadGame(request, localeProvider.getLocaleValidated());
    }
}
