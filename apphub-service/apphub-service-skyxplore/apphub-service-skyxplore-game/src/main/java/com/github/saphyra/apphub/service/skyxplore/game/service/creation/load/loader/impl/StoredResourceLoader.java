package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoredResourceLoader extends AutoLoader<StoredResourceModel, StoredResource> {
    public StoredResourceLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.STORED_RESOURCE;
    }

    @Override
    protected Class<StoredResourceModel[]> getArrayClass() {
        return StoredResourceModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<StoredResource> items) {
        gameData.getStoredResources()
            .addAll(items);
    }

    @Override
    protected StoredResource convert(StoredResourceModel model) {
        return StoredResource.builder()
            .storedResourceId(model.getId())
            .location(model.getLocation())
            .dataId(model.getDataId())
            .amount(model.getAmount())
            .containerId(model.getContainerId())
            .containerType(model.getContainerType())
            .allocatedBy(model.getAllocatedBy())
            .build();
    }
}
