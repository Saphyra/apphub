package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreCharacterDataController {
    @GetMapping(Endpoints.SKYXPLORE_GET_CHARACTER_NAME)
    OneParamResponse<String> getCharacterName(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER)
    void createOrUpdateCharacter(@RequestBody SkyXploreCharacterModel character, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_INTERNAL_GET_CHARACTER_BY_USER_ID)
    ResponseEntity<SkyXploreCharacterModel> internalGetCharacterByUserId(@PathVariable("userId") UUID userId);

    @GetMapping(Endpoints.SKYXPLORE_CHARACTER_EXISTS)
    OneParamResponse<Boolean> exists(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
