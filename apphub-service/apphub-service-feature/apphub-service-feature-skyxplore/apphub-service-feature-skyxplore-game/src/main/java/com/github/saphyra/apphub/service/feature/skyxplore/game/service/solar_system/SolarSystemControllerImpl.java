package com.github.saphyra.apphub.service.feature.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.SkyXploreGameSolarSystemController;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class SolarSystemControllerImpl implements SkyXploreGameSolarSystemController {
    private final SolarSystemResponseQueryService solarSystemResponseQueryService;
    private final RenameSolarSystemService renameSolarSystemService;

    @Override
    public SolarSystemResponse getSolarSystem(UUID solarSystemId, AccessToken accessToken) {
        log.info("{} wants to view solarSystem {}", accessToken.getUserId(), solarSystemId);
        return solarSystemResponseQueryService.getSolarSystem(accessToken.getUserId(), solarSystemId);
    }

    @Override
    public void renameSolarSystem(OneParamRequest<String> solarSystemName, UUID solarSystemId, AccessToken accessToken) {
        log.info("{} wants to rename solarSystem {}", accessToken.getUserId(), solarSystemId);
        renameSolarSystemService.rename(accessToken.getUserId(), solarSystemId, solarSystemName.getValue());
    }
}
