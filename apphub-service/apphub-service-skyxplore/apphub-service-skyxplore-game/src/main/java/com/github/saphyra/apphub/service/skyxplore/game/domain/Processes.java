package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class Processes extends Vector<Process> {
    public synchronized Process findByIdValidated(UUID processId) {
        return stream()
            .filter(process -> process.getProcessId().equals(processId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Process not found with id " + processId));
    }

    public synchronized Process findByExternalReferenceAndTypeValidated(UUID externalReference, ProcessType type) {
        List<Process> processes = getByExternalReferenceAndType(externalReference, type);

        if (processes.size() > 1) {
            throw new IllegalStateException("There are " + processes.size() + " number of Processes for externalReference " + externalReference + " and type " + type);
        }

        if (processes.isEmpty()) {
            throw new IllegalStateException("No Process found for externalReference " + externalReference + " and type " + type);
        }

        return processes.get(0);
    }

    public synchronized Optional<Process> findByExternalReferenceAndType(UUID externalReference, ProcessType type) {
        List<Process> processes = getByExternalReferenceAndType(externalReference, type);

        if (processes.size() > 1) {
            throw new IllegalStateException("There are " + processes.size() + " number of Processes for externalReference " + externalReference + " and type " + type);
        }

        return processes.size() == 1 ? Optional.of(processes.get(0)) : Optional.empty();
    }

    public synchronized List<Process> getByExternalReferenceAndType(UUID externalReference, ProcessType type) {
        return stream()
            .filter(process -> process.getExternalReference().equals(externalReference))
            .filter(process -> process.getType() == type)
            .collect(Collectors.toList());
    }

    public synchronized List<Process> getByExternalReference(UUID externalReference) {
        return stream()
            .filter(process -> process.getExternalReference().equals(externalReference))
            .collect(Collectors.toList());
    }
}
