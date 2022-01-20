package com.github.saphyra.apphub.service.skyxplore.game.tick.planet;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.PlanetTickTaskCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlanetTickProcessor {
    private final List<PlanetTickTaskCollector> taskCollectors;
    private final ErrorReporterService errorReporterService;

    /*
    Collecting tasks for the given planet, and process them
     */
    public void processForPlanet(UUID gameId, Planet planet) {
        taskCollectors.stream()
            .flatMap(planetTickTaskCollector -> planetTickTaskCollector.getTasks(gameId, planet).stream())
            .sorted((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()))
            .forEach(this::process);
    }

    private void process(TickTask tickTask) {
        try {
            tickTask.process();
        } catch (RuntimeException e) {
            errorReporterService.report("Failed processing tickTask", e);
        }
    }
}
