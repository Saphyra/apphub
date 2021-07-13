package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
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
class SurfaceLoader {
    private final GameItemLoader gameItemLoader;
    private final CoordinateLoader coordinateLoader;
    private final BuildingLoader buildingLoader;

    Map<Coordinate, Surface> load(UUID planetId) {
        List<SurfaceModel> models = gameItemLoader.loadChildren(planetId, GameItemType.SURFACE, SurfaceModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toMap(surface -> surface.getCoordinate().getCoordinate(), Function.identity()));
    }

    private Surface convert(SurfaceModel model) {
        return Surface.builder()
            .surfaceId(model.getId())
            .planetId(model.getPlanetId())
            .coordinate(coordinateLoader.loadOneByReferenceId(model.getId()))
            .surfaceType(SurfaceType.valueOf(model.getSurfaceType()))
            .building(buildingLoader.load(model.getId()))
            .build();
    }
}
