package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StoredResourcesFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredResourceLoader {
    private final GameItemLoader gameItemLoader;
    private final StoredResourcesFactory storedResourcesFactory;

    StoredResources load(UUID gameId, UUID location) {
        StoredResources storedResources = storedResourcesFactory.create(gameId, location, LocationType.PLANET);

        gameItemLoader.loadChildren(location, GameItemType.STORED_RESOURCE, StoredResourceModel[].class)
            .forEach(storedResourceModel -> storedResources.put(storedResourceModel.getDataId(), convert(storedResourceModel)));

        return storedResources;
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
