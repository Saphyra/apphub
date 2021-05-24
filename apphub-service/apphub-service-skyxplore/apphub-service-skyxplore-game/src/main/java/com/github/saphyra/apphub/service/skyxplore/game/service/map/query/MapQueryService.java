package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class MapQueryService {
    private final GameDao gameDao;
    private final UniverseToMapConverter universeToMapConverter;

    public MapResponse getMap(UUID userId) {
        Game game = gameDao.findByUserIdValidated(userId);
        return universeToMapConverter.convert(game.getUniverse());
    }
}
