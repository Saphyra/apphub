package com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class Processes extends Vector<Process> {
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

    public synchronized Process findByIdValidated(UUID processId) {
        return findById(processId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "No process found by processId " + processId));
    }

    private synchronized Optional<Process> findById(UUID processId) {
        return stream()
            .filter(process -> process.getProcessId().equals(processId))
            .findAny();
    }

    public synchronized Process findByExternalReferenceAndTypeValidated(UUID externalReference, ProcessType processType) {
        return findByExternalReferenceAndType(externalReference, processType)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "No process found with externalReference " + externalReference + " and processType " + processType));
    }

    public synchronized Optional<Process> findByExternalReferenceAndType(UUID externalReference, ProcessType processType) {
        return stream()
            .filter(process -> process.getExternalReference().equals(externalReference))
            .filter(process -> process.getType() == processType)
            .findAny();
    }

    public synchronized Process getRootOf(Process process) {
        Process root = process;
        while (true) {
            Optional<Process> maybeParent = findById(root.getExternalReference());
            log.debug("MaybeParent of {} - {}: {} - {}", root.getProcessId(), root.getType(), maybeParent.map(Process::getProcessId), maybeParent.map(Process::getType));
            if (maybeParent.isPresent()) {
                root = maybeParent.get();
            } else {
                log.debug("Root is: {} - {}", root.getProcessId(), root.getType());
                return root;
            }
        }
    }
}
