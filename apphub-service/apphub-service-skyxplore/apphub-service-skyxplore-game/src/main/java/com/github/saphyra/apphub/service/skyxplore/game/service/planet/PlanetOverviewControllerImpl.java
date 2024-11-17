package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.planet.SkyXplorePlanetOverviewController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetOverviewResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlanetOverviewControllerImpl implements SkyXplorePlanetOverviewController {
    private final PlanetOverviewQueryService planetOverviewQueryService;
    private final RenamePlanetService renamePlanetService;

    @Override
    public PlanetOverviewResponse getPlanetOverview(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the overview of planet {}", accessTokenHeader.getUserId(), planetId);
        return planetOverviewQueryService.getOverview(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    public void renamePlanet(OneParamRequest<String> planetName, UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to rename planet {}", accessTokenHeader.getUserId(), planetId);
        renamePlanetService.rename(accessTokenHeader.getUserId(), planetId, planetName.getValue());
    }
}
