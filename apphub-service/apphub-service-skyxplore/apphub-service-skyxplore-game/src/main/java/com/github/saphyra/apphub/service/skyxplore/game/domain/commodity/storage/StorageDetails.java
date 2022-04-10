package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StorageDetails {
    @Builder.Default
    private final AllocatedResources allocatedResources = new AllocatedResources();

    @Builder.Default
    private final ReservedStorages reservedStorages = new ReservedStorages();

    private final StoredResources storedResources;

    @Builder.Default
    private final StorageSettings storageSettings = new StorageSettings();

    @Override
    public String toString() {
        return String.format("StorageDetails(%s)", new Gson().toJson(this));
    }
}
