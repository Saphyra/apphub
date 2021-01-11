package com.github.saphyra.apphub.service.skyxplore.game.service.planet.command;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class RenameCitizenService {
    private final GameDao gameDao;

    public void renameCitizen(UUID userId, UUID planetId, UUID citizenId, String newName) {
        //TODO validate newName
        gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPopulation()
            .getOptional(citizenId)
            .orElseThrow(() -> new RuntimeException("Citizen not found with id " + citizenId + " on planet " + planetId))
            .setName(newName);
    }
}
