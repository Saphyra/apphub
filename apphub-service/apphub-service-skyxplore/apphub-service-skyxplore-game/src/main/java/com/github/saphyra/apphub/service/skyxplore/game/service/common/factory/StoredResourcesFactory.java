package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoredResourcesFactory {
    private final StoredResourceFactory storedResourceFactory;

    public StoredResources create(UUID gameId, UUID location, LocationType locationType) {
        return StoredResources.builder()
            .storedResourceFactory(storedResourceFactory)
            .gameId(gameId)
            .location(location)
            .locationType(locationType)
            .build();
    }
}
