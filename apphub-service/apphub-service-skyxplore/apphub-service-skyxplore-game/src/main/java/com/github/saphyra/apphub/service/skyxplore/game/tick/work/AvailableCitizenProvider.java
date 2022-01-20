package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AvailableCitizenProvider {
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    /*
    Finding the most efficient citizen for the given task
     */
    public Optional<Citizen> findMostCapableUnemployedCitizen(Collection<UUID> employedCitizens, Collection<Citizen> citizens, SkillType skillType) {
        return citizens.stream()
            .filter(citizen -> !employedCitizens.contains(citizen.getCitizenId()))
            .filter(citizen -> citizen.getMorale() <= 0)
            .max(Comparator.comparingDouble(o -> citizenEfficiencyCalculator.calculateEfficiency(o, skillType)));
    }
}
