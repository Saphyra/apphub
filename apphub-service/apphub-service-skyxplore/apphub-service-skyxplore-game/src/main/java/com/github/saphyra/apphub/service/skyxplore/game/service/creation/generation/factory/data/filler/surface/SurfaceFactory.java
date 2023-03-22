package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SurfaceFactory {
    private final IdGenerator idGenerator;

    Surface create(UUID planetId, SurfaceType surfaceType) {
        return Surface.builder()
            .surfaceId(idGenerator.randomUuid())
            .planetId(planetId)
            .surfaceType(surfaceType)
            .build();
    }
}
