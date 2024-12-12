package com.github.saphyra.apphub.service.skyxplore.game.service.map;

import com.github.saphyra.apphub.api.skyxplore.game.server.game.SkyXploreGameMapController;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.game.service.map.query.MapQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MapControllerImpl implements SkyXploreGameMapController {
    private final MapQueryService mapQueryService;

    @Override
    public MapResponse getMap(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his map.", accessTokenHeader.getUserId());
        return mapQueryService.getMap(accessTokenHeader.getUserId());
    }
}
