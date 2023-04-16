package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class ReservedStorages extends Vector<ReservedStorage> {
    public ReservedStorage findByReservedStorageIdValidated(UUID reservedStorageId) {
        return findByReservedStorageId(reservedStorageId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ReservedStorage not found by reservedStorageId " + reservedStorageId));
    }

    public Optional<ReservedStorage> findByReservedStorageId(UUID reservedStorageId) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getReservedStorageId().equals(reservedStorageId))
            .findAny();
    }

    //TODO unit test
    public ReservedStorage findByExternalReferenceValidated(UUID externalReference) {
        return getByExternalReference(externalReference)
            .stream()
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ReservedStorage not found by externalReference " + externalReference));
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
