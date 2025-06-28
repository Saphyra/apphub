package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductionRequestConverterTest {
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final int REQUESTED_AMOUNT = 234;
    private static final int DISPATCHED_AMOUNT = 43;

    @InjectMocks
    private ProductionRequestConverter underTest;

    @Test
    void toModel() {
        ProductionRequest productionRequest = ProductionRequest.builder()
            .productionRequestId(PRODUCTION_REQUEST_ID)
            .reservedStorageId(RESERVED_STORAGE_ID)
            .requestedAmount(REQUESTED_AMOUNT)
            .dispatchedAmount(DISPATCHED_AMOUNT)
            .build();

        assertThat(underTest.toModel(GAME_ID, productionRequest))
            .returns(PRODUCTION_REQUEST_ID, GameItem::getId)
            .returns(GameItemType.PRODUCTION_REQUEST, GameItem::getType)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(RESERVED_STORAGE_ID, ProductionRequestModel::getReservedStorageId)
            .returns(REQUESTED_AMOUNT, ProductionRequestModel::getRequestedAmount)
            .returns(DISPATCHED_AMOUNT, ProductionRequestModel::getDispatchedAmount);
    }
}