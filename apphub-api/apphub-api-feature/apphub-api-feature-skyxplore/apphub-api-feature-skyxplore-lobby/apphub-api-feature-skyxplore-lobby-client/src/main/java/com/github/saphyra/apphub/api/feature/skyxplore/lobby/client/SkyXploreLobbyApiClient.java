package com.github.saphyra.apphub.api.feature.skyxplore.lobby.client;

import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreLobbyEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

public interface SkyXploreLobbyApiClient {
    @PostExchange(SkyXploreLobbyEndpoints.SKYXPLORE_INTERNAL_GAME_LOADED)
    void gameLoaded(@PathVariable("gameId") UUID gameId);
}
