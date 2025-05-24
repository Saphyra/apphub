package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.logistics.LogisticsService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ResourceRequestProcessHelper {
    private final StoredResourceService storedResourceService;
    private final LogisticsService logisticsService;
    private final ProductionRequestFactory productionRequestFactory;

    public int getMissingResources(GameData gameData, UUID reservedStorageId) {
        int needed = gameData.getReservedStorages()
            .findByIdValidated(reservedStorageId)
            .getAmount();

        int onTheWay = countOnTheWay(gameData, reservedStorageId);

        int productionRequested = countProductionRequested(gameData, reservedStorageId);

        int result = needed - onTheWay - productionRequested;

        log.info("Resource needed: {}, On the way: {}, Production Requested: {}, remaining: {}", needed, onTheWay, productionRequested, result);

        return result;
    }

    private int countProductionRequested(GameData gameData, UUID reservedStorageId) {
        return gameData.getProductionRequests()
            .getByReservedStorageId(reservedStorageId)
            .stream()
            .mapToInt(ProductionRequest::getRequestedAmount)
            .sum();
    }

    private int countOnTheWay(GameData gameData, UUID reservedStorageId) {
        return gameData.getResourceDeliveryRequests()
            .getByReservedStorageId(reservedStorageId)
            .stream()
            .flatMap(resourceDeliveryRequest -> gameData.getConvoys().getByResourceDeliveryRequestId(resourceDeliveryRequest.getResourceDeliveryRequestId()).stream())
            .map(convoy -> gameData.getStoredResources().findByAllocatedByValidated(convoy.getConvoyId()))
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public int initiateDelivery(Game game, UUID processId, UUID location, UUID reservedStorageId, int missingResourceAmount) {
        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        String dataId = gameData.getReservedStorages()
            .findByIdValidated(reservedStorageId)
            .getDataId();

        List<StoredResource> resourcesToDeliver = storedResourceService.prepareBatch(progressDiff, gameData, location, dataId, missingResourceAmount);

        logisticsService.requestResourceDelivery(game, processId, location, reservedStorageId, resourcesToDeliver);

        return resourcesToDeliver.stream()
            .mapToInt(StoredResource::getAmount)
            .sum();
    }

    public void createProductionRequest(GameProgressDiff progressDiff, GameData gameData, UUID reservedStorageId, int requestedAmount) {
        ProductionRequest productionRequest = productionRequestFactory.save(progressDiff, gameData, reservedStorageId, requestedAmount);

        //TODO create ProductionDispatcherProcess
    }
}
