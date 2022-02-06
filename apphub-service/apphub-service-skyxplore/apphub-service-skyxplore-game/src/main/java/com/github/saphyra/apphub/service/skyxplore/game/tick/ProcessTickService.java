package com.github.saphyra.apphub.service.skyxplore.game.tick;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.PlanetTickProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ProcessTickService {
    private final TickCache tickCache;
    private final PlanetTickProcessor planetTickProcessor;
    private final ErrorReporterService errorReporterService;
    private final TickCacheItemFactory tickCacheItemFactory;

    void processTick(Game game) {
        if (shouldProcessTick(game)) {
            log.debug("TickProcession started for game {}", game.getGameId());
            tickCache.put(game.getGameId(), tickCacheItemFactory.create());

            game.getUniverse().getSystems()
                .values()
                .stream()
                .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
                .forEach(planet -> processPlanet(game.getGameId(), planet));

            tickCache.process(game.getGameId());

            log.debug("TickProcession finished for game {}", game.getGameId());
        }
    }

    private boolean shouldProcessTick(Game game) {
        if (game.isGamePaused()) {
            log.debug("Game {} is paused.", game.getGameId());
            return false;
        }

        if (game.getPlayers().values().stream().anyMatch(player -> !player.isConnected())) {
            log.debug("Not all players connected to game {}", game.getGameId());
            return false;
        }
        log.info("Processing tick for game {}", game.getGameId());
        return true;
    }

    private void processPlanet(UUID gameId, Planet planet) {
        try {
            synchronized (planet) {
                planetTickProcessor.processForPlanet(gameId, planet);
                log.debug("{} after processing tick in game {}", planet, gameId);
            }
        } catch (RuntimeException e) {
            errorReporterService.report("Failed processing tick for planet " + planet.getPlanetId(), e);
        }
    }

    @Component
    static class TickCacheItemFactory {
        TickCacheItem create() {
            return new TickCacheItem();
        }
    }
}
