package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public
class ReservedStorageFactory {
    private final IdGenerator idGenerator;

    public ReservedStorage create(UUID location, LocationType locationType, UUID externalReference, String dataId, int amount) {
        return ReservedStorage.builder()
            .reservedStorageId(idGenerator.randomUuid())
            .location(location)
            .locationType(locationType)
            .externalReference(externalReference)
            .dataId(dataId)
            .amount(amount)
            .build();
    }
}
