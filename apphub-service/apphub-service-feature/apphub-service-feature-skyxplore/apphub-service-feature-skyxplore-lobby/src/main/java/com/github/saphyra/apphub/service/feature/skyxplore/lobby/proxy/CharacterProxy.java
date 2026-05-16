package com.github.saphyra.apphub.service.feature.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.feature.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.feature.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class CharacterProxy {
    private final SkyXploreCharacterDataApiClient characterClient;
    private final AccessTokenProvider accessTokenProvider;

    public SkyXploreCharacterModel getCharacter() {
        return getCharacter(accessTokenProvider.get().getUserId());
    }

    public SkyXploreCharacterModel getCharacter(UUID userId) {
        return characterClient.internalGetCharacterByUserId(userId);
    }
}
