package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ResourceDeliveryRequestModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.resource_delivery_request.ResourceDeliveryRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
//TODO unit test
class ResourceDeliveryRequestLoader extends AutoLoader<ResourceDeliveryRequestModel, ResourceDeliveryRequest> {
    ResourceDeliveryRequestLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
    }

    @Override
    protected Class<ResourceDeliveryRequestModel[]> getArrayClass() {
    }

    @Override
    protected void addToGameData(GameData gameData, List<ResourceDeliveryRequest> items) {

    }

    @Override
    protected ResourceDeliveryRequest convert(ResourceDeliveryRequestModel resourceDeliveryRequestModel) {
    }
}
