package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage_setting;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.ReservedStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class ReservedStorageFactory {
    private final IdGenerator idGenerator;

    ReservedStorage create(UUID externalReference, String dataId, int amount) {
        return ReservedStorage.builder()
            .reservedStorageId(idGenerator.randomUuid())
            .externalReference(externalReference)
            .dataId(dataId)
            .amount(amount)
            .build();
    }
}
