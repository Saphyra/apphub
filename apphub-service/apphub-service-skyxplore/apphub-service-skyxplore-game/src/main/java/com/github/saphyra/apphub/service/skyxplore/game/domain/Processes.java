package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class Processes extends Vector<Process> {
    @Override
    public synchronized boolean add(Process process) {
        log.info("Adding process with id={}, externalReference={} and type={}", process.getProcessId(), process.getExternalReference(), process.getType());
        return super.add(process);
    }

    public synchronized Process findByIdValidated(UUID processId) {
        return stream()
            .filter(process -> process.getProcessId().equals(processId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Process not found with id " + processId));
    }

    public synchronized Process findByExternalReferenceAndTypeValidated(UUID externalReference, ProcessType type) {
        log.info("Looking for process with externalReference {} and type {}", elementData, type);
        List<Process> processes = getByExternalReferenceAndType(externalReference, type);

        if (processes.size() > 1) {
            throw ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, "There are " + processes.size() + " number of Processes for externalReference " + externalReference + " and type " + type);
        }

        if (processes.isEmpty()) {
            throw ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "No Process found for externalReference " + externalReference + " and type " + type);
        }

        return processes.get(0);
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
