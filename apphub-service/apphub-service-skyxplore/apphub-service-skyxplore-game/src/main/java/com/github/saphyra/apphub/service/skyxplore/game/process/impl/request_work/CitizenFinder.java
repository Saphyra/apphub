package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CitizenFinder {
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    public Optional<UUID> getSuitableCitizen(Planet planet, SkillType requiredSkill) {
        return planet.getPopulation()
            .values()
            .stream()
            .filter(citizen -> isNull(planet.getCitizenAllocations().get(citizen.getCitizenId())))
            .filter(citizen -> citizen.getMorale() > 0)
            .max(Comparator.comparingDouble(citizen -> citizenEfficiencyCalculator.calculateEfficiency(citizen, requiredSkill)))
            .map(Citizen::getCitizenId);
    }
}
