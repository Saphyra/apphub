package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ReservedStorages extends Vector<ReservedStorage> {
    public ReservedStorages(Collection<ReservedStorage> reservedStorages) {
        addAll(reservedStorages);
    }

    public ReservedStorage findByIdValidated(UUID reservedStorageId) {
        return findById(reservedStorageId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ReservedStorage not found with id " + reservedStorageId));
    }

    public Optional<ReservedStorage> findById(UUID reservedStorageId) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getReservedStorageId().equals(reservedStorageId))
            .findFirst();
    }

    public List<ReservedStorage> getByExternalReference(UUID externalReference) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference))
            .collect(Collectors.toList());
    }
}
