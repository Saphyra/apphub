package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class StoredResources extends Vector<StoredResource> {
    public synchronized List<StoredResource> getByLocation(UUID location) {
        return stream()
            .filter(storedResource -> storedResource.getLocation().equals(location))
            .collect(Collectors.toList());
    }

    @Override
    public synchronized String toString() {
        return new Gson().toJson(this);
    }

    public synchronized List<StoredResource> getByLocationAndDataId(UUID location, String dataId) {
        return stream()
            .filter(storedResource -> storedResource.getLocation().equals(location))
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .toList();
    }

    public synchronized List<StoredResource> getByContainerId(UUID containerId) {
        return stream()
            .filter(storedResource -> storedResource.getContainerId().equals(containerId))
            .toList();
    }

    public synchronized List<StoredResource> getByAllocatedBy(UUID externalReference) {
        return stream()
            .filter(storedResource -> externalReference.equals(storedResource.getAllocatedBy()))
            .toList();
    }

    public synchronized Optional<StoredResource> findByAllocatedBy(UUID allocatedBy) {
        return stream()
            .filter(storedResource -> nonNull(storedResource.getAllocatedBy()))
            .filter(storedResource -> storedResource.getAllocatedBy().equals(allocatedBy))
            .findAny();
    }

    public synchronized StoredResource findByAllocatedByValidated(UUID allocatedBy) {
        return findByAllocatedBy(allocatedBy)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StoredResource not found by allocatedBy " + allocatedBy));
    }

    public synchronized StoredResource findByContainerIdValidated(UUID containerId) {
        return findByContainerId(containerId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StoredResource not found by containerId " + containerId));
    }

    public synchronized Optional<StoredResource> findByContainerId(UUID containerId) {
        return stream()
            .filter(storedResource -> storedResource.getContainerId().equals(containerId))
            .findAny();
    }
}
