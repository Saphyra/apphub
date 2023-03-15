package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StorageDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StorageDetailsFactory {
    private final StoredResourcesFactory storedResourcesFactory;

    public StorageDetails create(UUID gameId, UUID location, LocationType locationType) {
        return StorageDetails.builder()
            .storedResources(storedResourcesFactory.create(gameId, location, locationType))
            .build();
    }
}
