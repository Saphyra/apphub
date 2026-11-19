package com.github.saphyra.apphub.api.feature.skyxplore.game.client;

import com.github.saphyra.apphub.api.feature.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.feature.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.UUID;

public interface SkyXploreGameCreationApiClient {
    @PutExchange(SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_CREATE_GAME)
    UUID createGame(@RequestBody SkyXploreGameCreationRequest request);

    @PostExchange(SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_LOAD_GAME)
    void loadGame(@RequestBody SkyXploreLoadGameRequest request);
}
