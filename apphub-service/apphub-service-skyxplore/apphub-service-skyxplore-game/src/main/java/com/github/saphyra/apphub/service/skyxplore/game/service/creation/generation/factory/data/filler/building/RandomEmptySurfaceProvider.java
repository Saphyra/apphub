package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
class RandomEmptySurfaceProvider {
    Surface getRandomEmptySurface(Collection<Surface> surfaces, GameData gameData) {
        return surfaces.stream()
            .filter(surface -> surface.getSurfaceType().equals(SurfaceType.DESERT))
            .filter(surface -> gameData.getBuildings().findBySurfaceId(surface.getSurfaceId()).isEmpty())
            .findAny()
            .orElseThrow(() -> new IllegalStateException("There are no empty surfaces left."));
    }
}
