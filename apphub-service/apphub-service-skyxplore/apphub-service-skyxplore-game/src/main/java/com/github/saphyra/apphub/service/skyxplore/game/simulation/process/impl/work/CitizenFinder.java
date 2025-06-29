package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenEfficiencyCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenFinder {
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    Optional<UUID> getSuitableCitizen(GameData gameData, UUID location, SkillType requiredSkill, int requestedWorkPoints) {
        return gameData.getCitizens()
            .getByLocation(location)
            .stream()
            .peek(citizen -> log.debug("Citizen at {}: {}", location, citizen))
            .filter(citizen -> gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isEmpty())
            .peek(citizen -> log.debug("Unallocated Citizen at {}: {}", location, citizen))
            .filter(citizen -> citizen.getMorale() >= citizenEfficiencyCalculator.calculateMoraleRequirement(gameData, citizen, requiredSkill, requestedWorkPoints))
            .peek(citizen -> log.debug("Relaxed Citizen at {}: {}", location, citizen))
            .max(Comparator.comparingDouble(citizen -> citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, requiredSkill)))
            .map(Citizen::getCitizenId);
    }
}
