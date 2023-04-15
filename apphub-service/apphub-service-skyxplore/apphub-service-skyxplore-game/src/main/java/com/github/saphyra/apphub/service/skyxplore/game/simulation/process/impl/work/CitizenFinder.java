package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
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

    public Optional<UUID> getSuitableCitizen(GameData gameData, UUID location, SkillType requiredSkill, int requestedWorkPoints) {
        return gameData.getCitizens()
            .getByLocation(location)
            .stream()
            .filter(citizen -> gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isEmpty())
            .filter(citizen -> citizen.getMorale() >= citizenEfficiencyCalculator.calculateMoraleRequirement(gameData, citizen, requiredSkill, requestedWorkPoints)) //TODO fix condition - Citizen should have enough morale for the task, and the estimated work time must not exceed the maximum
            .max(Comparator.comparingDouble(citizen -> citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, requiredSkill)))
            .map(Citizen::getCitizenId);
    }
}
