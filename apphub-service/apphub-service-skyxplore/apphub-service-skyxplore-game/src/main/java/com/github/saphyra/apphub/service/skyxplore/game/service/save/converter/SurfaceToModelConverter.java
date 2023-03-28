package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
//TODO unit test
public class SurfaceToModelConverter {
    public List<SurfaceModel> convert(UUID gameId, Collection<Surface> surfaces) {
        return surfaces.stream()
            .map(surface -> convert(gameId, surface))
            .collect(Collectors.toList());
    }

    public SurfaceModel convert(UUID gameId, Surface surface) {
        SurfaceModel model = new SurfaceModel();
        model.setId(surface.getSurfaceId());
        model.setGameId(gameId);
        model.setType(GameItemType.SURFACE);
        model.setPlanetId(surface.getPlanetId());
        model.setSurfaceType(surface.getSurfaceType().name());
        return model;
    }
}
