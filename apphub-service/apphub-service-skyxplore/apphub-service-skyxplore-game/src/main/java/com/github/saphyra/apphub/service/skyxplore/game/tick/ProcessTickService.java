package com.github.saphyra.apphub.service.skyxplore.game.tick;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
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
//TODO unit test
class ProcessTickService {
    private final TickCache tickCache;
    private final PlanetTickProcessor planetTickProcessor;
    private final GameDataProxy gameDataProxy;
    private final ExecutorServiceBean executorServiceBean;
    private final ErrorReporterService errorReporterService;

    void processTick(Game game) {
        if (shouldProcessTick(game)) {
            tickCache.put(game.getGameId(), new TickCacheItem());

            game.getUniverse().getSystems()
                .values()
                .stream()
                .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
                .forEach(planet -> processPlanet(game.getGameId(), planet));

            tickCache.process(game.getGameId(), gameDataProxy, executorServiceBean);
        }
    }

    private boolean shouldProcessTick(Game game) {
        if (game.isGamePaused()) {
            log.info("Game {} is paused.", game.getGameId());
            return false;
        }

        if (game.getPlayers().values().stream().anyMatch(player -> !player.isConnected())) {
            log.info("Not all players connected to game {}", game.getGameId());
            return false;
        }
        log.info("Processing tick for game {}", game.getGameId());
        return true;
    }

    private void processPlanet(UUID gameId, Planet planet) {
        log.debug("Processing tick for planet {}", planet.getPlanetId());
        try {
            synchronized (planet) {
                planetTickProcessor.processForPlanet(gameId, planet);
            }
        } catch (RuntimeException e) {
            errorReporterService.report("Failed processing tick for planet " + planet.getPlanetId(), e);
        }
    }
}
