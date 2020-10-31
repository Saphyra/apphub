package com.github.saphyra.apphub.api.skyxplore.data.client;

import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("skyxplore-character")
public interface SkyXploreCharacterApiClient {
    @GetMapping(Endpoints.IS_SKYXPLORE_CHARACTER_EXISTS)
    boolean isCharacterExistsForUser(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
