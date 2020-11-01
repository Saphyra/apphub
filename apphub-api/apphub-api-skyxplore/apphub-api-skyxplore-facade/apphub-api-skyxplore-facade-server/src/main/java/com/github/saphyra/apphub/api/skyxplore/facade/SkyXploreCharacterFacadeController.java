package com.github.saphyra.apphub.api.skyxplore.facade;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SkyXploreCharacterFacadeController {
    @PostMapping(Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER)
    void createOrUpdateCharacter(@RequestBody SkyXploreCharacterModel character, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.SKYXPLORE_GET_CHARACTER)
    SkyXploreCharacterModel getCharacter(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
