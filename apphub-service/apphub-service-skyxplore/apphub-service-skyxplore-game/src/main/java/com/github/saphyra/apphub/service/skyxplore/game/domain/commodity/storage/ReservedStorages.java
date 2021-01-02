package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import java.util.UUID;
import java.util.Vector;

public class ReservedStorages extends Vector<ReservedStorage> {
    public void deleteByExternalReference(UUID externalReference) {
        removeIf(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference));
    }
}
