package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class MatchingSurfaceTypeFilter {
    List<Surface> getSurfacesWithMatchingType(Collection<Surface> surfaces, SurfaceType surfaceType) {
        return surfaces.stream()
            .filter(surface -> surface.getSurfaceType().equals(surfaceType))
            .collect(Collectors.toList());
    }
}
