package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.planet.SkyXplorePlanetOverviewController;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.planet.overview.PlanetOverviewResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
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
    public PlanetOverviewResponse getPlanetOverview(UUID planetId, AccessToken accessToken) {
        log.info("{} wants to query the overview of planet {}", accessToken.getUserId(), planetId);
        return planetOverviewQueryService.getOverview(accessToken.getUserId(), planetId);
    }

    @Override
    public void renamePlanet(OneParamRequest<String> planetName, UUID planetId, AccessToken accessToken) {
        log.info("{} wants to rename planet {}", accessToken.getUserId(), planetId);
        renamePlanetService.rename(accessToken.getUserId(), planetId, planetName.getValue());
    }
}
