package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Vector;

@NoArgsConstructor
public class AllocatedResources extends Vector<AllocatedResource> {
    public AllocatedResources(Collection<AllocatedResource> allocatedResources) {
        addAll(allocatedResources);
    }
}
