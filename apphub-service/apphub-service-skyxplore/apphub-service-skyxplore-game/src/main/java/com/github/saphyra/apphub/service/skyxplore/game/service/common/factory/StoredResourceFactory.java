package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredResourceFactory {
    private final IdGenerator idGenerator;

    public StoredResource create(UUID location,  String dataId, int amount) {
        return StoredResource.builder()
            .storedResourceId(idGenerator.randomUuid())
            .location(location)
            .dataId(dataId)
            .amount(amount)
            .build();
    }
}
