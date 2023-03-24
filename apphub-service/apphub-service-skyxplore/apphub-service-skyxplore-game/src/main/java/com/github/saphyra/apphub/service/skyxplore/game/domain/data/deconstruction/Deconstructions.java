package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class Deconstructions extends Vector<Deconstruction> {
    public Deconstruction findByExternalReferenceValidated(UUID externalReference) {
        return findByExternalReference(externalReference)
            .orElseThrow();
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
            .orElseThrow();
    }

    public List<Deconstruction> getByLocation(UUID location) {
        return stream()
            .filter(deconstruction -> deconstruction.getLocation().equals(location))
            .collect(Collectors.toList());
    }
}
