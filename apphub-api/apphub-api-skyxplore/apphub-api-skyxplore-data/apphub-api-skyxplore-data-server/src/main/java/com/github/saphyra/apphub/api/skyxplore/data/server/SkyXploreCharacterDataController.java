package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface SkyXploreCharacterDataController {
    @GetMapping(Endpoints.INTERNAL_SKYXPLORE_IS_CHARACTER_EXISTS)
    boolean isCharacterExistsForUser(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_GET_CHARACTER)
    ResponseEntity<SkyXploreCharacterModel> getCharacter(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_CREATE_OR_UPDATE_CHARACTER)
    void createOrUpdateCharacter(@RequestBody SkyXploreCharacterModel character, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.INTERNAL_SKYXPLORE_GET_CHARACTER_BY_USER_ID)
    ResponseEntity<SkyXploreCharacterModel> internalGetCharacterByUserId(@PathVariable("userId") UUID userId);
}
