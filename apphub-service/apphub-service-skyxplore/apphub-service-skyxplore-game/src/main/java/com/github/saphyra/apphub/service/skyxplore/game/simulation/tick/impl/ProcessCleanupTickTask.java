package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ProcessCleanupTickTask implements TickTask {
    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.PROCESS_CLEANUP;
    }

    @Override
    public void process(Game game, SyncCache syncCache) {
        log.info("Cleaning up finished processes of game {}", game.getGameId());

        game.getData()
            .getProcesses()
            .stream()
            .filter(process -> process.getStatus() == ProcessStatus.DONE)
            .forEach(process -> game.getEventLoop().process(() -> process.cleanup(syncCache), syncCache));
    }
}
