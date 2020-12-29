package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Vector;

@Data
@AllArgsConstructor
@Builder
public class StorageDetails {
    private final List<AllocatedResource> allocatedResources = new Vector<>();
    private final List<ReservedStorage> reservedStorages = new Vector<>();
    private final List<StoredResource> storedResources = new Vector<>();
    private final List<StorageSetting> storageSettings = new Vector<>();
}
