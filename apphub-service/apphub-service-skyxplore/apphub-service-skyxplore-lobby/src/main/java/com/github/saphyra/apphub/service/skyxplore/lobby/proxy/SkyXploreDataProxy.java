package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataFriendApiClient;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SkyXploreDataProxy {
    private final SkyXploreDataFriendApiClient dataFriendClient;
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final LocaleProvider localeProvider;

    public List<FriendshipResponse> getFriends(AccessTokenHeader accessTokenHeader) {
        return dataFriendClient.getFriends(
            accessTokenHeaderConverter.convertDomain(accessTokenHeader),
            localeProvider.getLocaleValidated()
        );
    }
}
