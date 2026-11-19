package com.github.saphyra.apphub.api.feature.skyxplore.data.client;

import com.github.saphyra.apphub.api.feature.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.feature.skyxplore.SkyXploreDataEndpoints;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface SkyXploreDataFriendApiClient {
    @GetExchange(SkyXploreDataEndpoints.SKYXPLORE_GET_FRIENDS)
    List<FriendshipResponse> getFriends(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);
}
