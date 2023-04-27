package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.FreeStorageQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceAllocationService {
    private final FreeStorageQueryService freeStorageQueryService;
    private final ConsumptionCalculator consumptionCalculator;
    private final RequiredEmptyStorageCalculator requiredEmptyStorageCalculator;

    public void processResourceRequirements(SyncCache syncCache, GameData gameData, UUID location, UUID ownerId, UUID externalReference, Map<String, Integer> requiredResources) {
        Map<String, ConsumptionResult> consumptions = requiredResources.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> consumptionCalculator.calculate(gameData, location, externalReference, entry.getKey(), entry.getValue())));

        Map<StorageType, Integer> requiredStorageAmount = Arrays.stream(StorageType.values())
            .collect(Collectors.toMap(Function.identity(), storageType -> requiredEmptyStorageCalculator.getRequiredStorageAmount(storageType, consumptions)));

        requiredStorageAmount.forEach((storageType, totalAmount) -> {
            int freeStorage = freeStorageQueryService.getFreeStorage(gameData, location, storageType);
            if (freeStorage < totalAmount) {
                Map<String, String> params = CollectionUtils.singleValueMap("storageType", storageType.name());
                throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_ENOUGH_STORAGE, params, "Not enough free " + storageType + " storage to store " + totalAmount + " of resources.");
            }
        });

        consumptions.values()
            .forEach(consumptionResult -> {
                gameData.getAllocatedResources().add(consumptionResult.getAllocation());
                gameData.getReservedStorages().add(consumptionResult.getReservation());

                syncCache.resourceAllocated(ownerId, location, consumptionResult.getAllocation(), consumptionResult.getReservation());
            });
    }
}
