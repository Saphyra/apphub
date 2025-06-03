package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ProductionRequestConverter {
    public ProductionRequestModel toModel(UUID gameId, ProductionRequest request) {
        ProductionRequestModel model = new ProductionRequestModel();
        model.setId(request.getProductionRequestId());
        model.setGameId(gameId);
        model.setType(GameItemType.PRODUCTION_REQUEST);
        model.setReservedStorageId(request.getReservedStorageId());
        model.setRequestedAmount(request.getRequestedAmount());
        model.setDispatchedAmount(request.getDispatchedAmount());

        return model;
    }
}
