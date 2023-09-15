package com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class SolarSystemConverter implements GameDataToModelConverter {
    public List<SolarSystemModel> toModel(UUID gameId, List<SolarSystem> solarSystems) {
        return solarSystems.stream()
            .map(solarSystem -> toModel(gameId, solarSystem))
            .collect(Collectors.toList());
    }

    public SolarSystemModel toModel(UUID gameId, SolarSystem solarSystem) {
        SolarSystemModel model = new SolarSystemModel();
        model.setId(solarSystem.getSolarSystemId());
        model.setGameId(gameId);
        model.setType(GameItemType.SOLAR_SYSTEM);
        model.setRadius(solarSystem.getRadius());
        model.setDefaultName(solarSystem.getDefaultName());
        model.setCustomNames(solarSystem.getCustomNames());

        return model;
    }

    @Override
    public List<SolarSystemModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getSolarSystems());
    }
}
