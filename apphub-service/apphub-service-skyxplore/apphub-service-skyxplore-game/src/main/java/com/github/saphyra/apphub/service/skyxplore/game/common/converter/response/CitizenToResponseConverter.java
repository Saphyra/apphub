package com.github.saphyra.apphub.service.skyxplore.game.common.converter.response;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenToResponseConverter {
    private final SkillToResponseConverter skillToResponseConverter;

    public CitizenResponse convert(GameData gameData, Citizen citizen) {
        return CitizenResponse.builder()
            .citizenId(citizen.getCitizenId())
            .name(citizen.getName())
            .morale(citizen.getMorale())
            .satiety(citizen.getSatiety())
            .skills(skillToResponseConverter.getSkills(gameData, citizen.getCitizenId()))
            .build();
    }
}
