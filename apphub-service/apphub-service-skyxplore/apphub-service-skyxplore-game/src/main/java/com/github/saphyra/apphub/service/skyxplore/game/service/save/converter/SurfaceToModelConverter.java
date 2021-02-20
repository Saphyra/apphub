package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SurfaceToModelConverter {
    private final BuildingToModelConverter buildingConverter;

    public List<GameItem> convertDeep(Collection<Surface> surfaces, Game game) {
        return surfaces.stream()
            .map(surface -> convertDeep(surface, game))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public List<GameItem> convertDeep(Surface surface, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(surface, game));
        if (!isNull(surface.getBuilding())) {
            result.add(buildingConverter.convert(surface.getBuilding(), game));
        }
        return result;
    }

    private SurfaceModel convert(Surface surface, Game game) {
        SurfaceModel model = new SurfaceModel();
        model.setId(surface.getSurfaceId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.SURFACE);
        model.setPlanetId(surface.getPlanetId());
        model.setSurfaceType(surface.getSurfaceType().name());
        model.setCoordinate(surface.getCoordinate());
        return model;
    }
}