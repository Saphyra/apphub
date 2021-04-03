package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StorageDetails {
    private final List<AllocatedResource> allocatedResources = new Vector<>();
    private final ReservedStorages reservedStorages = new ReservedStorages();
    private final Map<String, StoredResource> storedResources = new ConcurrentHashMap<>();
    private final StorageSettings storageSettings = new StorageSettings();
}
