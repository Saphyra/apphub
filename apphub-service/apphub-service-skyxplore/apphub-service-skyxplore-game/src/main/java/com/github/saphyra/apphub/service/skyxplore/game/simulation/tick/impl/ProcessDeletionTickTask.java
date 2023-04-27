package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessDeletionTickTask implements TickTask {
    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.PROCESS_DELETION;
    }

    @Override
    public void process(Game game, SyncCache syncCache) {
        log.info("Deleting cleaned up processes of game {}", game.getGameId());

        Processes processes = game.getData()
            .getProcesses();
        List<Process> processesToRemove = processes.stream()
            .filter(process -> process.getStatus() == ProcessStatus.READY_TO_DELETE)
            .toList();

        processesToRemove.stream()
            .peek(process -> process.cleanup(syncCache))
            .forEach(process -> syncCache.deleteGameItem(process.getProcessId(), GameItemType.PROCESS));
        processes.removeAll(processesToRemove);
    }
}
