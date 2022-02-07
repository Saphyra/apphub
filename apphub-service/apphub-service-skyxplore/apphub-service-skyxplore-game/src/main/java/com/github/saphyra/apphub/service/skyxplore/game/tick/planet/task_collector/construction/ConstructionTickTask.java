package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction.ConstructionTickProcessor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder
@RequiredArgsConstructor
@Data
class ConstructionTickTask implements TickTask {
    @NotNull
    private final UUID gameId;

    @NotNull
    private final Planet planet;

    @NotNull
    private final Surface surface;

    private final int priority;

    @NotNull
    private final ConstructionTickProcessor constructionTickProcessor;

    @Override
    public void process() {
        constructionTickProcessor.process(gameId, planet, surface);
    }
}
