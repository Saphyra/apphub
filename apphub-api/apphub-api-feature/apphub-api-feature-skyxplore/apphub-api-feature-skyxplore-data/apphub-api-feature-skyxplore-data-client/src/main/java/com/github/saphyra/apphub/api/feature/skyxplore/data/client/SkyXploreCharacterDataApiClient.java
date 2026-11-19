package com.github.saphyra.apphub.api.feature.skyxplore.data.client;

import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreDataEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.UUID;

public interface SkyXploreCharacterDataApiClient {
    @GetExchange(SkyXploreDataEndpoints.SKYXPLORE_INTERNAL_GET_CHARACTER_BY_USER_ID)
    SkyXploreCharacterModel internalGetCharacterByUserId(@PathVariable("userId") UUID userId);
}
