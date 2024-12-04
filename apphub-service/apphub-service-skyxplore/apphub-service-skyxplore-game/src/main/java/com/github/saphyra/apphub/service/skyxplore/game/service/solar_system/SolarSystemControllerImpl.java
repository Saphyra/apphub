package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.game.server.game.solar_system.SkyXploreGameSolarSystemController;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.SolarSystemResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
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
    public SolarSystemResponse getSolarSystem(UUID solarSystemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to view solarSystem {}", accessTokenHeader.getUserId(), solarSystemId);
        return solarSystemResponseQueryService.getSolarSystem(accessTokenHeader.getUserId(), solarSystemId);
    }

    @Override
    public void renameSolarSystem(OneParamRequest<String> solarSystemName, UUID solarSystemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to rename solarSystem {}", accessTokenHeader.getUserId(), solarSystemId);
        renameSolarSystemService.rename(accessTokenHeader.getUserId(), solarSystemId, solarSystemName.getValue());
    }
}
