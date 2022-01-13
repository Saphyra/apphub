package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.consumption;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelAllocationsService {
    private final GameDataProxy gameDataProxy;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;
    private final WsMessageSender messageSender;

    public void cancelAllocationsAndReservations(Planet planet, UUID externalReference) {
        StorageDetails storageDetails = planet.getStorageDetails();
        synchronized (storageDetails) {
            List<ReservedStorage> reservedStorages = storageDetails.getReservedStorages()
                .stream()
                .filter(reservedStorage -> reservedStorage.getExternalReference().equals(externalReference))
                .peek(reservedStorage -> gameDataProxy.deleteItem(reservedStorage.getReservedStorageId(), GameItemType.RESERVED_STORAGE))
                .collect(Collectors.toList());
            storageDetails.getReservedStorages()
                .removeAll(reservedStorages);

            List<AllocatedResource> allocatedResources = storageDetails.getAllocatedResources()
                .stream()
                .filter(allocatedResource -> allocatedResource.getExternalReference().equals(externalReference))
                .peek(allocatedResource -> gameDataProxy.deleteItem(allocatedResource.getAllocatedResourceId(), GameItemType.ALLOCATED_RESOURCE))
                .collect(Collectors.toList());
            storageDetails.getAllocatedResources()
                .removeAll(allocatedResources);
            messageSender.planetStorageModified(planet.getOwner(), planet.getPlanetId(), planetStorageOverviewQueryService.getStorage(planet));
        }
    }
}
