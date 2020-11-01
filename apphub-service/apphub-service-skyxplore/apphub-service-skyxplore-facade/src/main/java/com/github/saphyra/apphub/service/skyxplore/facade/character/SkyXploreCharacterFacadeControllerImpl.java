package com.github.saphyra.apphub.service.skyxplore.facade.character;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.facade.SkyXploreCharacterFacadeController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreCharacterFacadeControllerImpl implements SkyXploreCharacterFacadeController {
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterDataApiClient characterDataClient;

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    public void createOrUpdateCharacter(SkyXploreCharacterModel character, AccessTokenHeader accessTokenHeader) {
        //TODO validate request
        characterDataClient.createOrUpdateCharacter(
            character,
            accessTokenHeaderConverter.convertDomain(accessTokenHeader),
            localeProvider.getLocaleValidated()
        );
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    public SkyXploreCharacterModel getCharacter(AccessTokenHeader accessTokenHeader) {
        return characterDataClient.getCharacter(
            accessTokenHeaderConverter.convertDomain(accessTokenHeader),
            localeProvider.getLocaleValidated()
        );
    }
}
