package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class ReservedStorages extends Vector<ReservedStorage> {
    public Optional<ReservedStorage> findById(UUID reservedStorageId) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getReservedStorageId().equals(reservedStorageId))
            .findAny();
    }

    public List<ReservedStorage> getByExternalReference(UUID externalReference) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference))
            .collect(Collectors.toList());
    }

    public List<ReservedStorage> getByLocation(UUID location) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getLocation().equals(location))
            .collect(Collectors.toList());
    }
}
