package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@Builder
public class StorageDetails {
    @Builder.Default
    private final AllocatedResources allocatedResources = new AllocatedResources();

    @Builder.Default
    private final ReservedStorages reservedStorages = new ReservedStorages();

    @Builder.Default
    private final Map<String, StoredResource> storedResources = new ConcurrentHashMap<>();

    @Builder.Default
    private final StorageSettings storageSettings = new StorageSettings();

    @Override
    public String toString() {
        return String.format("StorageDetails(%s)", new Gson().toJson(this));
    }
}
