package com.github.saphyra.apphub.service.skyxplore.game.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;

import java.util.UUID;

public interface Process extends Comparable<Process> {
    UUID getProcessId();

    UUID getExternalReference();

    int getPriority();

    ProcessStatus getStatus();

    ProcessType getType();

    default void scheduleWork(SyncCache syncCache) {
        if (getStatus() == ProcessStatus.READY_TO_DELETE) {
            return;
        }

        work(syncCache);

        syncCache.saveGameItem(toModel());
    }

    void work(SyncCache syncCache);

    void cancel(SyncCache syncCache);

    void cleanup(SyncCache syncCache);

    ProcessModel toModel();

    default int compareTo(Process o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
