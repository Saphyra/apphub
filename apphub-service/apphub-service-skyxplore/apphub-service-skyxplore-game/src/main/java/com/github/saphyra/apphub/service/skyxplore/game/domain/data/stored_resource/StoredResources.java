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

public class StoredResources extends Vector<StoredResource> {
    @Override
    public boolean add(StoredResource storedResource) {
        if (findByLocationAndDataId(storedResource.getLocation(), storedResource.getDataId()).isPresent()) {
            throw new RuntimeException(String.format("StoredResource with dataId %s already exist at %s", storedResource.getDataId(), storedResource.getLocation()));
        }

        return super.add(storedResource);
    }

    public Optional<StoredResource> findByLocationAndDataId(UUID location, String dataId) {
        return stream()
            .filter(storedResource -> storedResource.getLocation().equals(location))
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .findAny();
    }

    public StoredResource findByLocationAndDataIdValidated(UUID location, String dataId) {
        return findByLocationAndDataId(location, dataId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StoredResource not found by location " + location +  " and dataId " + dataId));
    }

    public List<StoredResource> getByLocation(UUID location) {
        return stream()
            .filter(storedResource -> storedResource.getLocation().equals(location))
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
