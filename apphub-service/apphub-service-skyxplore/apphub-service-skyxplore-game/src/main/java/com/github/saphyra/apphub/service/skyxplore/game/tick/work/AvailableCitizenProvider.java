package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AvailableCitizenProvider {
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    /*
    Finding the most efficient citizen for the given task
     */
    public Optional<Citizen> findMostCapableUnemployedCitizen(Map<UUID, Assignment> employedCitizens, Collection<Citizen> citizens, @Nullable UUID location, SkillType skillType) {
        Optional<Citizen> result = citizens.stream()
            .filter(citizen -> citizen.getMorale() > 0)
            .filter(citizen -> isCitizenFree(citizen, employedCitizens, location))
            .max(Comparator.comparingDouble(o -> citizenEfficiencyCalculator.calculateEfficiency(o, skillType)));
        log.debug("The most efficient citizen for skillType {} is {}", skillType, result);
        return result;
    }

    private boolean isCitizenFree(Citizen citizen, Map<UUID, Assignment> employedCitizens, @Nullable UUID location) {
        Assignment assignment = employedCitizens.get(citizen.getCitizenId());
        if (isNull(assignment)) {
            return true;
        }

        log.debug("Citizen {} is already assigned to {}", citizen.getCitizenId(), assignment);
        boolean result = assignment.getLocation().equals(location) && assignment.getWorkPointsLeft() > 0;
        log.debug("Citizen {} can work for location {}: {}", citizen.getCitizenId(), location, result);
        return result;
    }
}
