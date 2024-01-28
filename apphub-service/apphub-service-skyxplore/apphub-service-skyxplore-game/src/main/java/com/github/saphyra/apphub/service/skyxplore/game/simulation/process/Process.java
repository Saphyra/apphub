package com.github.saphyra.apphub.service.skyxplore.game.simulation.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;

import java.util.UUID;

public interface Process extends Comparable<Process> {
    ProcessType getType();

    UUID getProcessId();

    UUID getExternalReference();

    int getPriority();

    ProcessStatus getStatus();

    default void scheduleWork(GameProgressDiff gameProgressDiff) {
        if (getStatus() == ProcessStatus.READY_TO_DELETE) {
            return;
        }

        work();

        gameProgressDiff.save(toModel());
    }

    void work();

    void cleanup();

    ProcessModel toModel();

    default int compareTo(Process o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
