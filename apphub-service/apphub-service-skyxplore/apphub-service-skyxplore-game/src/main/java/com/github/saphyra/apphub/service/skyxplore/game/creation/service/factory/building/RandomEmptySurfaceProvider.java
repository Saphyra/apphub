package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class RandomEmptySurfaceProvider {
    Surface getRandomEmptySurface(Collection<Surface> surfaces) {
        return surfaces.stream()
            .filter(surface -> surface.getSurfaceType().equals(SurfaceType.DESERT))
            .filter(surface -> isNull(surface.getBuilding()))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("There are no empty surfaces left."));
    }
}
