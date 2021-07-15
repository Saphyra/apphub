package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredResourceLoader {
    private final GameItemLoader gameItemLoader;

    Map<String, StoredResource> load(UUID location) {
        List<StoredResourceModel> models = gameItemLoader.loadChildren(location, GameItemType.STORED_RESOURCE, StoredResourceModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toMap(StoredResource::getDataId, Function.identity(), (u, v) -> {
                throw new IllegalStateException(String.format("Duplicate key %s", u));
            }, ConcurrentHashMap::new));
    }

    private StoredResource convert(StoredResourceModel model) {
        return StoredResource.builder()
            .storedResourceId(model.getId())
            .location(model.getLocation())
            .locationType(LocationType.valueOf(model.getLocationType()))
            .dataId(model.getDataId())
            .amount(model.getAmount())
            .build();
    }
}
