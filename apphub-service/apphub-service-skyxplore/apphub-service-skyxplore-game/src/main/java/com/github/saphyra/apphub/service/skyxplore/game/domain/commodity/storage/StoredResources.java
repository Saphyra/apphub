package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Builder
@Slf4j
public class StoredResources extends ConcurrentHashMap<String, StoredResource> {
    private final StoredResourceFactory storedResourceFactory;
    private final UUID gameId;
    private final UUID location;
    private final LocationType locationType;

    @Override
    public StoredResource get(Object key) {
        StoredResource result = super.get(key);
        if (isNull(result)) {
            StoredResource storedResource = storedResourceFactory.create(location, locationType, key.toString(), 0);
            put(storedResource.getDataId(), storedResource);
            return storedResource;
        }
        return result;
    }
}
