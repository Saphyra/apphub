package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
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
public class CitizenToModelConverter {
    public List<CitizenModel> convert(UUID gameId, Collection<Citizen> citizens) {
        return citizens.stream()
            .map(citizen -> convert(gameId, citizen))
            .collect(Collectors.toList());
    }

    public CitizenModel convert(UUID gameId, Citizen citizen) {
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
}
