package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

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

    //TODO unit test
    public List<AllocatedResource> getByExternalReference(UUID externalReference) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getExternalReference().equals(externalReference))
            .collect(Collectors.toList());
    }

    //TODO unit test
    public AllocatedResource findByIdValidated(UUID allocatedResourceId) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getAllocatedResourceId().equals(allocatedResourceId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "AllocatedResource not found with id " + allocatedResourceId));
    }
}
