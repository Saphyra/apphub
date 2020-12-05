package com.github.saphyra.apphub.api.skyxplore.data.client;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("skyxplore-data-character")
public interface SkyXploreCharacterDataApiClient {
    @GetMapping(Endpoints.SKYXPLORE_IS_CHARACTER_EXISTS)
    boolean isCharacterExistsForUser(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(Endpoints.SKYXPLORE_GET_CHARACTER)
    SkyXploreCharacterModel getCharacter(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(Endpoints.INTERNAL_SKYXPLORE_GET_CHARACTER_BY_USER_ID)
    SkyXploreCharacterModel internalGetCharacterByUserId(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
