package com.github.saphyra.apphub.service.skyxplore.game.service.planet.logistics;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_delivery.ResourceDeliveryProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogisticsService {
    private final ResourceDeliveryRequestFactory resourceDeliveryRequestFactory;
    private final StoredResourceConverter storedResourceConverter;
    private final ResourceDeliveryProcessFactory resourceDeliveryProcessFactory;

    public void requestResourceDelivery(Game game, UUID processId, UUID location, UUID reservedStorageId, List<StoredResource> resourcesToDeliver) {
        resourcesToDeliver.forEach(storedResource -> requestResourceDelivery(game, processId, location, reservedStorageId, storedResource));
    }

    private void requestResourceDelivery(Game game, UUID processId, UUID location, UUID reservedStorageId, StoredResource storedResource) {
        GameProgressDiff progressDiff = game.getProgressDiff();
        GameData gameData = game.getData();

        ResourceDeliveryRequest deliveryRequest = resourceDeliveryRequestFactory.save(progressDiff, gameData, reservedStorageId);

        storedResource.setAllocatedBy(deliveryRequest.getResourceDeliveryRequestId());
        progressDiff.save(storedResourceConverter.toModel(gameData.getGameId(), storedResource));

        resourceDeliveryProcessFactory.save(game, location, processId, deliveryRequest.getResourceDeliveryRequestId());
    }
}
