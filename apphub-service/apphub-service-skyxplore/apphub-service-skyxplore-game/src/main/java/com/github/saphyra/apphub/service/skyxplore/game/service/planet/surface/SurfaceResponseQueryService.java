package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurfaceResponseQueryService {
    private final SurfaceConverter surfaceConverter;
    private final GameDao gameDao;

    public List<SurfaceResponse> getSurfaceOfPlanet(UUID userId, UUID planetId) {
        GameData gameData = gameDao.findByUserIdValidated(userId)
            .getData();
        return gameData.getSurfaces()
            .getByPlanetId(planetId)
            .stream()
            .map(surface -> surfaceConverter.toResponse(gameData, surface))
            .collect(Collectors.toList());
    }
}
