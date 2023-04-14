package com.github.saphyra.apphub.service.skyxplore.game.simulation.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;

import java.util.UUID;

public interface Process extends Comparable<Process> {
    ProcessType getType();

    UUID getProcessId();

    UUID getExternalReference();

    int getPriority();

    ProcessStatus getStatus();

    default void scheduleWork(SyncCache syncCache) {
        if (getStatus() == ProcessStatus.READY_TO_DELETE) {
            return;
        }

        work(syncCache);

        syncCache.saveGameItem(toModel());
    }

    void work(SyncCache syncCache);

    void cancel(SyncCache syncCache);

    default void cleanup(SyncCache syncCache) {
    }

    ProcessModel toModel();

    default int compareTo(Process o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
