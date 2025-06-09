package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
//TODO unit test
class ProductionRequestLoader extends AutoLoader<ProductionRequestModel, ProductionRequest> {
    ProductionRequestLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.PRODUCTION_REQUEST;
    }

    @Override
    protected Class<ProductionRequestModel[]> getArrayClass() {
        return ProductionRequestModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<ProductionRequest> items) {
        gameData.getProductionRequests()
            .addAll(items);
    }

    @Override
    protected ProductionRequest convert(ProductionRequestModel model) {
        return ProductionRequest.builder()
            .productionRequestId(model.getId())
            .reservedStorageId(model.getReservedStorageId())
            .requestedAmount(model.getRequestedAmount())
            .dispatchedAmount(model.getDispatchedAmount())
            .build();
    }
}
