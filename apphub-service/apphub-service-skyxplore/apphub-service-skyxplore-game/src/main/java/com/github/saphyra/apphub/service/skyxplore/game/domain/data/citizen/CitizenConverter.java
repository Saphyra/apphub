package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.CitizenAssignmentProvider;
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
public class CitizenConverter implements GameDataToModelConverter {
    private final SkillConverter skillConverter;
    private final StatConverter statConverter;
    private final CitizenAssignmentProvider citizenAssignmentProvider;

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
            .skills(skillConverter.toResponse(gameData, citizen.getCitizenId()))
            .stats(statConverter.convert(citizen))
            .assignment(citizenAssignmentProvider.getAssignment(gameData, citizen.getCitizenId()))
            .build();
    }

    @Override
    public List<CitizenModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getCitizens());
    }
}
