package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class SolarSystemToModelConverter {
    private final PlanetToModelConverter planetToModelConverter;

    public List<GameItem> convertDeep(Collection<SolarSystem> systems, Game game) {
        return systems.stream()
            .map(solarSystem -> convertDeep(solarSystem, game))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<GameItem> convertDeep(SolarSystem system, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(system, game));
        result.add(system.getCoordinate());
        result.addAll(planetToModelConverter.convertDeep(system.getPlanets().values(), game));
        return result;
    }

    private SolarSystemModel convert(SolarSystem system, Game game) {
        SolarSystemModel model = new SolarSystemModel();
        model.setId(system.getSolarSystemId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.SOLAR_SYSTEM);
        model.setRadius(system.getRadius());
        model.setDefaultName(system.getDefaultName());
        model.setCustomNames(system.getCustomNames());
        return model;
    }
}
