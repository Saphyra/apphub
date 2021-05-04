package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
public class StorageDetails {
    private final List<AllocatedResource> allocatedResources = new Vector<>();
    private final ReservedStorages reservedStorages = new ReservedStorages();
    private final Map<String, StoredResource> storedResources = new ConcurrentHashMap<>();
    private final StorageSettings storageSettings = new StorageSettings();
}
