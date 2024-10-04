package com.github.saphyra.apphub.api.skyxplore.data.client;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreDataEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "skyxplore-data-friend", url = "${serviceUrls.skyxploreData}")
public interface SkyXploreDataFriendApiClient {
    @GetMapping(SkyXploreDataEndpoints.SKYXPLORE_GET_FRIENDS)
    List<FriendshipResponse> getFriends(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
