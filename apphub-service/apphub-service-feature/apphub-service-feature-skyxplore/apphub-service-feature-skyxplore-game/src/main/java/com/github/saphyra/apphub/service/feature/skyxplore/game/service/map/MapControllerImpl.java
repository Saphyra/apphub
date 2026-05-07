package com.github.saphyra.apphub.service.feature.skyxplore.game.service.map;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.SkyXploreGameMapController;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.skyxplore.game.service.map.query.MapQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MapControllerImpl implements SkyXploreGameMapController {
    private final MapQueryService mapQueryService;

    @Override
    public MapResponse getMap(AccessToken accessToken) {
        log.info("{} wants to know his map.", accessToken.getUserId());
        return mapQueryService.getMap(accessToken.getUserId());
    }
}
