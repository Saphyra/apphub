package com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class Processes extends Vector<Process> {
    public List<Process> getByExternalReferenceAndType(UUID externalReference, ProcessType type) {
        return stream()
            .filter(process -> process.getExternalReference().equals(externalReference))
            .filter(process -> process.getType() == type)
            .collect(Collectors.toList());
    }

    public List<Process> getByExternalReference(UUID externalReference) {
        return stream()
            .filter(process -> process.getExternalReference().equals(externalReference))
            .collect(Collectors.toList());
    }
}
