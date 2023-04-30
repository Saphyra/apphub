package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class AllocatedResourcesLoader extends AutoLoader<AllocatedResourceModel, AllocatedResource> {
    public AllocatedResourcesLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.ALLOCATED_RESOURCE;
    }

    @Override
    protected Class<AllocatedResourceModel[]> getArrayClass() {
        return AllocatedResourceModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<AllocatedResource> items) {
        gameData.getAllocatedResources()
            .addAll(items);
    }

    @Override
    protected AllocatedResource convert(AllocatedResourceModel model) {
        return AllocatedResource.builder()
            .allocatedResourceId(model.getId())
            .location(model.getLocation())
            .externalReference(model.getExternalReference())
            .dataId(model.getDataId())
            .amount(model.getAmount())
            .build();
    }
}
