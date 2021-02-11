package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
//TODO unit test
public class PlanetToModelConverter {
    private final SurfaceToModelConverter surfaceConverter;
    private final CitizenToModelConverter citizenConverter;
    private final AllocatedResourceToModelConverter allocatedResourceConverter;
    private final ReservedStorageToModelConverter reservedStorageConverter;
    private final StoredResourceToModelConverter storedResourceConverter;
    private final StorageSettingToModelConverter storageSettingConverter;
    private final PriorityToModelConverter priorityConverter;

    public List<GameItem> convertDeep(Collection<Planet> planets, Game game) {
        return planets.stream()
            .map(planet -> convertDeep(planet, game))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public List<GameItem> convertDeep(Planet planet, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(planet, game));
        result.addAll(surfaceConverter.convertDeep(planet.getSurfaces().values(), game));
        result.addAll(citizenConverter.convertDeep(planet.getPopulation().values(), game));
        result.addAll(allocatedResourceConverter.convert(planet.getStorageDetails().getAllocatedResources(), game));
        result.addAll(reservedStorageConverter.convert(planet.getStorageDetails().getReservedStorages(), game));
        result.addAll(storedResourceConverter.convert(planet.getStorageDetails().getStoredResources(), game));
        result.addAll(storageSettingConverter.convert(planet.getStorageDetails().getStorageSettings(), game));
        result.addAll(priorityConverter.convert(planet.getPriorities(), planet.getPlanetId(), LocationType.PLANET, game));
        return result;
    }

    private PlanetModel convert(Planet planet, Game game) {
        PlanetModel model = new PlanetModel();
        model.setId(planet.getPlanetId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.PLANET);
        model.setSolarSystemId(planet.getSolarSystemId());
        model.setDefaultName(planet.getDefaultName());
        model.setCustomNames(planet.getCustomNames());
        model.setCoordinate(planet.getCoordinate());
        model.setSize(planet.getSize());
        model.setOwner(planet.getOwner());
        return model;
    }
}
