package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

@NoArgsConstructor
//TODO unit test
public class ReservedStorages extends Vector<ReservedStorage> {
    public ReservedStorages(Collection<ReservedStorage> reservedStorages) {
        addAll(reservedStorages);
    }

    public Optional<ReservedStorage> findById(UUID reservedStorageId) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getReservedStorageId().equals(reservedStorageId))
            .findFirst();
    }

    public ReservedStorage findByExternalReferenceAndDataIdValidated(UUID externalReference, String dataId) {
        return stream()
            .filter(reservedStorage -> reservedStorage.getDataId().equals(dataId))
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ReservedStorage not found with externalReference " + externalReference + " and dataId " + dataId));
    }
}
