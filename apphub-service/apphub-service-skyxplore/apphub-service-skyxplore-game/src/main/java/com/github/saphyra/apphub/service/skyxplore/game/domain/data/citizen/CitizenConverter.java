package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CitizenConverter {
    private final SkillConverter skillConverter;

    public List<CitizenModel> toModel(UUID gameId, Collection<Citizen> citizens) {
        return citizens.stream()
            .map(citizen -> toModel(gameId, citizen))
            .collect(Collectors.toList());
    }

    public CitizenModel toModel(UUID gameId, Citizen citizen) {
        CitizenModel model = new CitizenModel();
        model.setId(citizen.getCitizenId());
        model.setGameId(gameId);
        model.setType(GameItemType.CITIZEN);
        model.setLocation(citizen.getLocation());
        model.setName(citizen.getName());
        model.setMorale(citizen.getMorale());
        model.setSatiety(citizen.getSatiety());
        return model;
    }

    public List<CitizenResponse> toResponse(GameData gameData, List<Citizen> citizens) {
        return citizens.stream()
            .map(citizen -> toResponse(gameData, citizen))
            .collect(Collectors.toList());
    }

    public CitizenResponse toResponse(GameData gameData, Citizen citizen) {
        return CitizenResponse.builder()
            .citizenId(citizen.getCitizenId())
            .name(citizen.getName())
            .morale(citizen.getMorale())
            .satiety(citizen.getSatiety())
            .skills(skillConverter.toResponse(gameData, citizen.getCitizenId()))
            .build();
    }
}
