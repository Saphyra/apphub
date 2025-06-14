package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.storage.StorageBuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StoredResourceService {
    private final StorageBuildingModuleDataService storageBuildingModuleDataService;
    private final StoredResourceFactory storedResourceFactory;

    public List<StoredResource> prepareBatch(GameProgressDiff progressDiff, GameData gameData, UUID location, String dataId, int missingResourceAmount) {
        Map<UUID, List<StoredResource>> availableResources = gameData.getBuildingModules()
            .getByLocation(location)
            .stream()
            //Filter for storage building modules
            .filter(buildingModule -> storageBuildingModuleDataService.containsKey(buildingModule.getDataId()))
            //Filter for depots
            .filter(buildingModule -> GameConstants.DEPOT_BUILDING_MODULE_CATEGORIES.contains(storageBuildingModuleDataService.get(buildingModule.getDataId()).getCategory()))
            .map(BuildingModule::getBuildingModuleId)
            .flatMap(buildingModuleId -> gameData.getStoredResources().getByContainerId(buildingModuleId).stream())
            //Filter for available resources
            .filter(storedResource -> isNull(storedResource.getAllocatedBy()))
            //Filter for matching resources
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .collect(Collectors.groupingBy(StoredResource::getContainerId));

        List<StoredResource> aggregated = aggregate(progressDiff, gameData, availableResources);

        List<StoredResource> result = new ArrayList<>();
        for (StoredResource storedResource : aggregated) {
            if (missingResourceAmount > 0) {
                if (storedResource.getAmount() <= missingResourceAmount) {
                    result.add(storedResource);
                    missingResourceAmount -= storedResource.getAmount();
                } else {
                    int remaining = storedResource.getAmount();

                    result.add(split(progressDiff, gameData, storedResource, missingResourceAmount, remaining));
                }
            }
        }

        return result;
    }

    private StoredResource split(GameProgressDiff progressDiff, GameData gameData, StoredResource base, int missingResourceAmount, int remaining) {
        storedResourceFactory.save(progressDiff, gameData, base.getLocation(), base.getDataId(), remaining, base.getContainerId(), base.getContainerType());

        gameData.getStoredResources()
            .remove(base);
        progressDiff.delete(base.getStoredResourceId(), GameItemType.STORED_RESOURCE);

        return storedResourceFactory.save(progressDiff, gameData, base.getLocation(), base.getDataId(), missingResourceAmount, base.getContainerId(), base.getContainerType());
    }

    private List<StoredResource> aggregate(GameProgressDiff progressDiff, GameData gameData, Map<UUID, List<StoredResource>> availableResources) {
        return availableResources.values()
            .stream()
            .map(storedResources -> aggregate(progressDiff, gameData, storedResources))
            .toList();
    }

    private StoredResource aggregate(GameProgressDiff progressDiff, GameData gameData, List<StoredResource> storedResources) {
        if (storedResources.size() == 1) {
            return storedResources.get(0);
        }

        int totalResources = storedResources.stream()
            .mapToInt(StoredResource::getAmount)
            .sum();

        gameData.getStoredResources().removeAll(storedResources);
        storedResources.forEach(storedResource -> progressDiff.delete(storedResource.getStoredResourceId(), GameItemType.STORED_RESOURCE));

        StoredResource base = storedResources.get(0);

        return storedResourceFactory.save(progressDiff, gameData, base.getLocation(), base.getDataId(), totalResources, base.getContainerId(), base.getContainerType());
    }

    public int count(GameData gameData, String dataId, UUID allocatedBy) {
        return gameData.getStoredResources()
            .getByAllocatedBy(allocatedBy)
            .stream()
            .filter(storedResource -> storedResource.getDataId().equals(dataId))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public void useResources(GameProgressDiff progressDiff, GameData gameData, UUID allocatedBy) {
        gameData.getStoredResources()
            .getByAllocatedBy(allocatedBy)
            .forEach(storedResource -> {
                gameData.getStoredResources()
                    .remove(storedResource);
                progressDiff.delete(storedResource.getStoredResourceId(), GameItemType.STORED_RESOURCE);
            });
    }
}
