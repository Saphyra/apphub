package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourceFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class StoredResources extends ConcurrentHashMap<String, StoredResource> {
    @NonNull
    private final StoredResourceFactory storedResourceFactory;

    @NonNull
    private final UUID gameId;

    @NonNull
    private final UUID location;

    @NonNull
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
