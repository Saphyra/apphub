package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.game.solar_system.planet.SkyXplorePlanetPopulationController;
import com.github.saphyra.apphub.api.feature.skyxplore.response.game.citizen.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class SkyXplorePlanetPopulationControllerImpl implements SkyXplorePlanetPopulationController {
    private final PopulationQueryService populationQueryService;
    private final RenameCitizenService renameCitizenService;

    @Override
    public List<CitizenResponse> getPopulation(UUID planetId, AccessToken accessToken) {
        log.info("{} wants to know the population of planet {}", accessToken.getUserId(), planetId);
        return populationQueryService.getPopulation(accessToken.getUserId(), planetId);
    }

    @Override
    public void renameCitizen(OneParamRequest<String> newName, UUID citizenId, AccessToken accessToken) {
        log.info("{} wants to rename citizen {}", accessToken.getUserId(), citizenId);
         renameCitizenService.renameCitizen(accessToken.getUserId(), citizenId, newName.getValue());
    }
}
