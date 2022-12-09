package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXplorePlanetPopulationController;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
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
    public List<CitizenResponse> getPopulation(UUID planetId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the population of planet {}", accessTokenHeader.getUserId(), planetId);
        return populationQueryService.getPopulation(accessTokenHeader.getUserId(), planetId);
    }

    @Override
    public CitizenResponse renameCitizen(OneParamRequest<String> newName, UUID planetId, UUID citizenId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to rename citizen {} on planet {}", accessTokenHeader.getUserId(), citizenId, planetId);
        return renameCitizenService.renameCitizen(accessTokenHeader.getUserId(), planetId, citizenId, newName.getValue());
    }
}
