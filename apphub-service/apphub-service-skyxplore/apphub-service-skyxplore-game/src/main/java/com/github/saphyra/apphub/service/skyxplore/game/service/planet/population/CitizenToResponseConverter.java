package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenToResponseConverter {
    private final SkillToResponseConverter skillToResponseConverter;

    CitizenResponse convert(Citizen citizen) {
        return CitizenResponse.builder()
            .citizenId(citizen.getCitizenId())
            .name(citizen.getName())
            .morale(citizen.getMorale())
            .satiety(citizen.getSatiety())
            .skills(skillToResponseConverter.getSkills(citizen.getSkills()))
            .build();
    }
}
