package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
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

    public ReservedStorage create(UUID location, UUID externalReference, String dataId, int amount) {
        ReservedStorage result = ReservedStorage.builder()
            .reservedStorageId(idGenerator.randomUuid())
            .location(location)
            .externalReference(externalReference)
            .dataId(dataId)
            .amount(amount)
            .build();
        log.debug("{} is created.", result);
        return result;
    }
}
