package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import java.util.UUID;
import java.util.Vector;

public class ReservedStorages extends Vector<ReservedStorage> {
    public void deleteByExternalReference(UUID externalReference) {
        removeIf(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference));
    }

    public ReservedStorage findByExternalReferenceValidated(UUID externalReference) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("ReservedStorage not found with externalReference " + externalReference));
    }
}
