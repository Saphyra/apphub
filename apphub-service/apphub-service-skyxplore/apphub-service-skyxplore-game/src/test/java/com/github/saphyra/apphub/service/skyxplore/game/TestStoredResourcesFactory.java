package com.github.saphyra.apphub.service.skyxplore.game;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import org.mockito.Mockito;

import java.util.UUID;

public class TestStoredResourcesFactory {
    public static StoredResources create() {
        return StoredResources.builder()
            .storedResourceFactory(Mockito.mock(StoredResourceFactory.class))
            .gameId(UUID.randomUUID())
            .location(UUID.randomUUID())
            .locationType(LocationType.PLANET)
            .build();
    }
}
