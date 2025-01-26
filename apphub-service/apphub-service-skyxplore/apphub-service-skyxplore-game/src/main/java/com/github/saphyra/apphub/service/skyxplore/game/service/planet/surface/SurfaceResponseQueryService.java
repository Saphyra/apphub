package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SurfaceResponseQueryService {
    private final SurfaceConverter surfaceConverter;
    private final GameDao gameDao;

    public List<SurfaceResponse> getSurfaceOfPlanet(UUID userId, UUID planetId) {
        Optional<Game> maybeGame = gameDao.findByUserId(userId);
        if(maybeGame.isEmpty()){
            return Collections.emptyList();
        }
        GameData gameData = maybeGame.get()
            .getData();
        return gameData.getSurfaces()
            .getByPlanetId(planetId)
            .stream()
            .map(surface -> surfaceConverter.toResponse(gameData, surface))
            .collect(Collectors.toList());
    }
}
