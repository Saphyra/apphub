package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class StoredResources extends Vector<StoredResource> {
    public Optional<StoredResource> findByLocationAndDataId(UUID location, String dataId) {
        return stream()
            .filter(storedResource -> storedResource.getLocation().equals(location))
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .findAny();
    }
}
