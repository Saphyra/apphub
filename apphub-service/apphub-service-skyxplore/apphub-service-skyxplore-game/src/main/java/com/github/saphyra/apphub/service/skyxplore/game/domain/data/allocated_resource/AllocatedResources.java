package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class AllocatedResources extends Vector<AllocatedResource> {
    public List<AllocatedResource> getByLocationAndDataId(UUID location, String dataId) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getLocation().equals(location))
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .collect(Collectors.toList());
    }

    public AllocatedResource findByExternalReferenceAndDataIdValidated(UUID externalReference, String dataId) {
        return findByExternalReferenceAndDataId(externalReference, dataId)
            .orElseThrow(() -> ExceptionFactory.loggedException(
                HttpStatus.NOT_FOUND,
                ErrorCode.DATA_NOT_FOUND,
                "AllocatedResource not found by externalReference " + externalReference + " and dataId " + dataId)
            );
    }

    public Optional<AllocatedResource> findByExternalReferenceAndDataId(UUID externalReference, String dataId) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getExternalReference().equals(externalReference))
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .findFirst();
    }

    public List<AllocatedResource> getByExternalReference(UUID externalReference) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getExternalReference().equals(externalReference))
            .collect(Collectors.toList());
    }

    public Optional<AllocatedResource> findByAllocatedResourceId(UUID allocatedResourceId) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getAllocatedResourceId().equals(allocatedResourceId))
            .findAny();
    }

    public List<AllocatedResource> getByLocation(UUID location) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getLocation().equals(location))
            .collect(Collectors.toList());
    }
}
