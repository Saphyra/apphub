package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.StoredResourceToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StoredResourcesFactory {
    private final GameDataProxy gameDataProxy;
    private final StoredResourceToModelConverter storedResourceToModelConverter;
    private final StoredResourceFactory storedResourceFactory;

    public StoredResources create(UUID gameId, UUID location, LocationType locationType) {
        return StoredResources.builder()
            .gameDataProxy(gameDataProxy)
            .storedResourceToModelConverter(storedResourceToModelConverter)
            .storedResourceFactory(storedResourceFactory)
            .gameId(gameId)
            .location(location)
            .locationType(locationType)
            .build();
    }
}
