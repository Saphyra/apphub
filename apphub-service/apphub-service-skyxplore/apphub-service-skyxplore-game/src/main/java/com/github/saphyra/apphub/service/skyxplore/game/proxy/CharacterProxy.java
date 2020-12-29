package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CharacterProxy {
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterDataApiClient client;

    public SkyXploreCharacterModel getCharacterByUserId(UUID userId) {
        String locale = localeProvider.getOrDefault();
        log.debug("Querying character by userId {} with locale {}", userId, locale);
        SkyXploreCharacterModel skyXploreCharacterModel = client.internalGetCharacterByUserId(userId, locale);
        log.debug("Character found: {}", skyXploreCharacterModel);
        return skyXploreCharacterModel;
    }
}
