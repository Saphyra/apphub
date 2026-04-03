package com.github.saphyra.apphub.api.feature.skyxplore.game.server.platform;

import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataDetails;
import com.github.saphyra.apphub.api.feature.skyxplore.admin.SkyXploreGameDataEntry;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SkyXploreGameAdminController {
    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_ADMIN_GET_BY_TYPE)
    List<SkyXploreGameDataEntry> getByType(
        @PathVariable("type") GameItemType type,
        @RequestAttribute(name = "gameId", required = false) UUID gameId, //Null when querying available games
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );

    @GetMapping(SkyXploreGameEndpoints.SKYXPLORE_GAME_ADMIN_GET_ITEM)
    SkyXploreGameDataDetails getItem(
        @PathVariable("gameId") UUID gameId,
        @PathVariable("type") GameItemType type,
        @PathVariable("itemId") UUID itemId,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );
}
