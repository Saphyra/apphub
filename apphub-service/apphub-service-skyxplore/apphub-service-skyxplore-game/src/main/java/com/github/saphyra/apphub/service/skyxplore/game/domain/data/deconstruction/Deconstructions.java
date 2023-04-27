package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class Deconstructions extends Vector<Deconstruction> {
    public Deconstruction findByExternalReferenceValidated(UUID externalReference) {
        return findByExternalReference(externalReference)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Deconstruction not found by externalReference " + externalReference));
    }

    public Optional<Deconstruction> findByExternalReference(UUID externalReference) {
        return stream()
            .filter(deconstruction -> deconstruction.getExternalReference().equals(externalReference))
            .findAny();
    }

    public Deconstruction findByDeconstructionId(UUID deconstructionId) {
        return stream()
            .filter(deconstruction -> deconstruction.getDeconstructionId().equals(deconstructionId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Deconstruction not found by deconstructionId " + deconstructionId));
    }

    public List<Deconstruction> getByLocation(UUID location) {
        return stream()
            .filter(deconstruction -> deconstruction.getLocation().equals(location))
            .collect(Collectors.toList());
    }
}
