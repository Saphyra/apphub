package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.model.ProductionOrderToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetToModelConverter {
    private final SurfaceToModelConverter surfaceConverter;
    private final CitizenToModelConverter citizenConverter;
    private final PriorityToModelConverter priorityConverter;
    private final StorageDetailsToModelConverter storageDetailsConverter;
    private final ProductionOrderToModelConverter productionOrderToModelConverter;

    public List<GameItem> convertDeep(Collection<Planet> planets, Game game) {
        return planets.stream()
            .map(planet -> convertDeep(planet, game))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<GameItem> convertDeep(Planet planet, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(planet, game));
        result.addAll(surfaceConverter.convertDeep(planet.getSurfaces().values(), game));
        result.addAll(citizenConverter.convertDeep(planet.getPopulation().values(), game));
        result.addAll(storageDetailsConverter.convertDeep(planet.getStorageDetails(), game));
        result.addAll(priorityConverter.convert(planet.getPriorities(), planet.getPlanetId(), LocationType.PLANET, game));
        result.add(planet.getCoordinate());
        return result;
    }

    public PlanetModel convert(Planet planet, Game game) {
        PlanetModel model = new PlanetModel();
        model.setId(planet.getPlanetId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.PLANET);
        model.setSolarSystemId(planet.getSolarSystemId());
        model.setDefaultName(planet.getDefaultName());
        model.setCustomNames(planet.getCustomNames());
        model.setSize(planet.getSize());
        model.setOwner(planet.getOwner());
        model.setBuildingAllocations(planet.getBuildingAllocations());
        model.setCitizenAllocations(planet.getCitizenAllocations());
        return model;
    }
}
