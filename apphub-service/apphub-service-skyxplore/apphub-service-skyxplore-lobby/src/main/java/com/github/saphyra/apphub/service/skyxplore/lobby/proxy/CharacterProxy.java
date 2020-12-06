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
//TODO unit test
public class CharacterProxy {
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterDataApiClient characterClient;
    private final AccessTokenProvider accessTokenProvider;

    public SkyXploreCharacterModel getCharacter() {
        return characterClient.internalGetCharacterByUserId(
            accessTokenProvider.get().getUserId(),
            localeProvider.getLocaleValidated()
        );
    }

    public SkyXploreCharacterModel getCharacter(UUID userId) {
        return characterClient.internalGetCharacterByUserId(
            userId,
            localeProvider.getLocaleValidated()
        );
    }
}
