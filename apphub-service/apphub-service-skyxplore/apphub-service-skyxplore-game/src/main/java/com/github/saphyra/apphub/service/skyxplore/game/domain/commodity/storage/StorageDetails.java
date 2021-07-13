package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@Builder
public class StorageDetails {
    @Builder.Default
    private final List<AllocatedResource> allocatedResources = new Vector<>();

    @Builder.Default
    private final ReservedStorages reservedStorages = new ReservedStorages();

    @Builder.Default
    private final Map<String, StoredResource> storedResources = new ConcurrentHashMap<>();

    @Builder.Default
    private final StorageSettings storageSettings = new StorageSettings();
}
