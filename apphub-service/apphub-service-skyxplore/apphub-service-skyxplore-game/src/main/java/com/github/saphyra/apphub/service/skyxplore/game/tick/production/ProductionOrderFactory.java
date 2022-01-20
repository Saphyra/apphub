package com.github.saphyra.apphub.service.skyxplore.game.tick.production;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.ProductionOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ProductionOrderFactory {
    private final IdGenerator idGenerator;

    ProductionOrder create(UUID externalReference, UUID location, LocationType locationType, String dataId, int amount) {
        return ProductionOrder.builder()
            .productionOrderId(idGenerator.randomUuid())
            .location(location)
            .locationType(locationType)
            .externalReference(externalReference)
            .dataId(dataId)
            .amount(amount)
            .currentWorkPoints(0)
            .build();
    }
}
