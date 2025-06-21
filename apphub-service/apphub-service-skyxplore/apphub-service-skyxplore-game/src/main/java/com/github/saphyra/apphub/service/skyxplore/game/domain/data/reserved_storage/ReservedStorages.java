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
    public ReservedStorage findByIdValidated(UUID reservedStorageId) {
        return findById(reservedStorageId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ReservedStorage not found by reservedStorageId " + reservedStorageId));
    }

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

    //TODO unit test
    public List<ReservedStorage> getByContainerId(UUID containerId) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getContainerId().equals(containerId))
            .toList();
    }
}
