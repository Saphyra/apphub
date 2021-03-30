package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PopulationQueryService {
    private final GameDao gameDao;
    private final CitizenToResponseConverter citizenToResponseConverter;

    public List<CitizenResponse> getPopulation(UUID userId, UUID planetId) {
        return gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPopulation()
            .values()
            .stream()
            .map(citizenToResponseConverter::convert)
            .collect(Collectors.toList());
    }
}
