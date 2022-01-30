package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

@NoArgsConstructor
public class AllocatedResources extends Vector<AllocatedResource> {
    public AllocatedResources(Collection<AllocatedResource> allocatedResources) {
        addAll(allocatedResources);
    }

    public AllocatedResource findByExternalReferenceAndDataIdValidated(UUID externalReference, String dataId) {
        return stream()
            .filter(allocatedResource -> externalReference.equals(allocatedResource.getExternalReference()))
            .filter(allocatedResource -> dataId.equals(allocatedResource.getDataId()))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "AllocatedResource not found with externalReference " + externalReference + " and dataId " + dataId));
    }
}
