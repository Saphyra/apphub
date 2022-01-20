package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.tick.TickTask;

import java.util.List;
import java.util.UUID;

public interface PlanetTickTaskCollector {
    List<TickTask> getTasks(UUID gameId, Planet planet);
}
