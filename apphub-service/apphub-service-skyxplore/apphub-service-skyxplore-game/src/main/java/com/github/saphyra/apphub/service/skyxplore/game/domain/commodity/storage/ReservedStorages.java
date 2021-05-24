package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReservedStorages extends Vector<ReservedStorage> {
    public ReservedStorages(Collection<ReservedStorage> reservedStorages) {
        addAll(reservedStorages);
    }

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
