package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
class PlanetLoader {
    private final GameItemLoader gameItemLoader;
    private final CoordinateLoader coordinateLoader;
    private final SurfaceLoader surfaceLoader;
    private final CitizenLoader citizenLoader;
    private final PriorityLoader priorityLoader;
    private final StorageDetailsLoader storageDetailsLoader;

    Map<UUID, Planet> load(UUID solarSystemId) {
        List<PlanetModel> models = gameItemLoader.loadChildren(solarSystemId, GameItemType.PLANET, PlanetModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toMap(Planet::getPlanetId, Function.identity()));
    }

    private Planet convert(PlanetModel model) {
        return Planet.builder()
            .planetId(model.getId())
            .solarSystemId(model.getSolarSystemId())
            .defaultName(model.getDefaultName())
            .customNames(new OptionalHashMap<>(model.getCustomNames()))
            .coordinate(coordinateLoader.loadOneByReferenceId(model.getId()))
            .size(model.getSize())
            .surfaces(surfaceLoader.load(model.getId()))
            .owner(model.getOwner())
            .population(citizenLoader.load(model.getId()))
            .storageDetails(storageDetailsLoader.load(model.getId()))
            .priorities(priorityLoader.load(model.getId()))
            .build();
    }
}
