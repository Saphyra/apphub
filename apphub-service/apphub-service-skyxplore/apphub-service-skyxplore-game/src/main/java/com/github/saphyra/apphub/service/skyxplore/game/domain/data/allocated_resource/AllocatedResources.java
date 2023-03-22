package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class AllocatedResources extends Vector<AllocatedResource> {
    public List<AllocatedResource> getByLocationAndDataId(UUID location, String dataId) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getLocation().equals(location))
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .collect(Collectors.toList());
    }

    public Optional<AllocatedResource> findByExternalReferenceAndDataId(UUID externalReference, String dataId) {
        return stream()
            .filter(allocatedResource -> allocatedResource.getExternalReference().equals(externalReference))
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .findFirst();
    }
}
