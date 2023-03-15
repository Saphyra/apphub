package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurfaceToModelConverter {
    private final BuildingToModelConverter buildingConverter;
    private final ConstructionToModelConverter constructionToModelConverter;

    public List<GameItem> convertDeep(Collection<Surface> surfaces, Game game) {
        return surfaces.stream()
            .map(surface -> convertDeep(surface, game))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<GameItem> convertDeep(Surface surface, Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(surface, game.getGameId()));
        result.add(surface.getCoordinate());
        if (!isNull(surface.getBuilding())) {
            result.add(buildingConverter.convert(surface.getBuilding(), game.getGameId()));
        }
        if (!isNull(surface.getTerraformation())) {
            result.add(constructionToModelConverter.convert(surface.getTerraformation(), game.getGameId()));
        }
        return result;
    }

    public SurfaceModel convert(Surface surface, UUID gameId) {
        SurfaceModel model = new SurfaceModel();
        model.setId(surface.getSurfaceId());
        model.setGameId(gameId);
        model.setType(GameItemType.SURFACE);
        model.setPlanetId(surface.getPlanetId());
        model.setSurfaceType(surface.getSurfaceType().name());
        return model;
    }
}
