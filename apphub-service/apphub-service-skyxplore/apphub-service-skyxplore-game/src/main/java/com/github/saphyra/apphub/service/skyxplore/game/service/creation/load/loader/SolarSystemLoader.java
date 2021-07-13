package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemLoader {
    private final GameItemLoader gameItemLoader;
    private final CoordinateLoader coordinateLoader;
    private final PlanetLoader planetLoader;

    public Map<Coordinate, SolarSystem> load(UUID gameId) {
        List<SolarSystemModel> models = gameItemLoader.loadChildren(gameId, GameItemType.SOLAR_SYSTEM, SolarSystemModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toMap(solarSystem -> solarSystem.getCoordinate().getCoordinate(), Function.identity()));
    }

    private SolarSystem convert(SolarSystemModel model) {
        return SolarSystem.builder()
            .solarSystemId(model.getId())
            .radius(model.getRadius())
            .defaultName(model.getDefaultName())
            .customNames(new OptionalHashMap<>(model.getCustomNames()))
            .coordinate(coordinateLoader.loadOneByReferenceId(model.getId()))
            .planets(planetLoader.load(model.getId()))
            .build();
    }
}
