package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocatedResourceAmountQueryService {
    private final ResourceDataService resourceDataService;

    public int getAllocatedResourceAmount(GameData gameData, UUID location, String dataId) {
        return gameData.getAllocatedResources()
            .getByLocationAndDataId(location, dataId)
            .stream()
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }

    public int getAllocatedResourceAmount(String dataId, List<AllocatedResource> allocatedResources) {
        return allocatedResources.stream()
            .filter(allocatedResource -> allocatedResource.getDataId().equals(dataId))
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }

    public int getAllocatedResourceAmount(GameData gameData, UUID location, StorageType storageType) {
        List<String> dataIdsByStorageType = resourceDataService.getByStorageType(storageType)
            .stream()
            .map(GameDataItem::getId)
            .toList();

        return gameData.getAllocatedResources()
            .getByLocation(location)
            .stream()
            .filter(allocatedResource -> dataIdsByStorageType.contains(allocatedResource.getDataId()))
            .mapToInt(AllocatedResource::getAmount)
            .sum();
    }
}
