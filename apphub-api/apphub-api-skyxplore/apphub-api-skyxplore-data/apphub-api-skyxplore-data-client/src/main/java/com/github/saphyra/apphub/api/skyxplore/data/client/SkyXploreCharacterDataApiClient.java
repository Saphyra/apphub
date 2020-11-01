package com.github.saphyra.apphub.api.skyxplore.data.client;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("skyxplore-character")
public interface SkyXploreCharacterDataApiClient {
    @GetMapping(Endpoints.IS_SKYXPLORE_CHARACTER_EXISTS)
    boolean isCharacterExistsForUser(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_GET_CHARACTER)
    SkyXploreCharacterModel getCharacter(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_CREATE_OR_UPDATE_CHARACTER)
    void createOrUpdateCharacter(@RequestBody SkyXploreCharacterModel character, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
