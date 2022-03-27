package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.AllocatedResourceToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.ReservedStorageToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewlyProducedResourceAllocationService {
    private final TickCache tickCache;
    private final ReservedStorageToModelConverter reservedStorageToModelConverter;
    private final AllocatedResourceToModelConverter allocatedResourceToModelConverter;

    /*
    Reduce reservedStorage with the given amount
    Increase the allocated amount.
     */
    void allocateNewlyProducedResource(UUID gameId, Planet planet, ProductionOrder order, ReservedStorage reservedStorage) {
        log.debug("{} before finishing {} in game {}", reservedStorage, order, gameId);
        reservedStorage.reduceAmount(order.getAmount());

        GameItemCache gameItemCache = tickCache.get(gameId)
            .getGameItemCache();
        gameItemCache.save(reservedStorageToModelConverter.convert(reservedStorage, gameId));

        AllocatedResource allocatedResource = planet.getStorageDetails()
            .getAllocatedResources()
            .findByExternalReferenceAndDataIdValidated(reservedStorage.getExternalReference(), reservedStorage.getDataId());
        allocatedResource.increaseAmount(order.getAmount());

        log.debug("{}, {} after finishing {} in game {}", allocatedResource, reservedStorage, order, gameId);

        gameItemCache.save(allocatedResourceToModelConverter.convert(allocatedResource, gameId));
    }
}
