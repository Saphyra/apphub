package com.github.saphyra.apphub.service.skyxplore.facade.friend;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreFriendDataApiClient;
import com.github.saphyra.apphub.api.skyxplore.facade.SkyXploreFriendFacadeController;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreFriendFacadeControllerImpl implements SkyXploreFriendFacadeController {
    private final SkyXploreFriendDataApiClient friendClient;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;

    @Override
    //TODO unit test
    //TODO api test
    //TODO int test
    public List<SkyXploreCharacterModel> getFriends(OneParamRequest<String> queryString, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get the characters with name {}", accessTokenHeader.getUserId(), queryString.getValue());
        return friendClient.getFriends(queryString, accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated());
    }

    @Override
    //TODO unit test
    //TODO api test
    //TODO int test
    public void addFriend(OneParamRequest<UUID> characterId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add {} as friend", accessTokenHeader.getUserId(), characterId.getValue());
        friendClient.addFriend(characterId, accessTokenHeaderConverter.convertDomain(accessTokenHeader), localeProvider.getLocaleValidated());
    }
}
