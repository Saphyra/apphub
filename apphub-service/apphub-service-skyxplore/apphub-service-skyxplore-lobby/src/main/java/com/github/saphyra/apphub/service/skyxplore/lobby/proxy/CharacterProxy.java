package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class CharacterProxy {
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterDataApiClient characterClient;
    private final AccessTokenProvider accessTokenProvider;

    public SkyXploreCharacterModel getCharacter() {
        return getCharacter(accessTokenProvider.get().getUserId());
    }

    public SkyXploreCharacterModel getCharacter(UUID userId) {
        return characterClient.internalGetCharacterByUserId(
            userId,
            localeProvider.getLocaleValidated()
        );
    }
}
