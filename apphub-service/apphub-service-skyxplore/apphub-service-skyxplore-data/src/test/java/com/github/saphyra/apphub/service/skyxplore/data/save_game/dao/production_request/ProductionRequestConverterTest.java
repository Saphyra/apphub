package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionRequestConverterTest {
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final Integer REQUESTED_AMOUNT = 324;
    private static final Integer DISPATCHED_AMOUNT = 23;
    private static final String PRODUCTION_REQUEST_ID_STRING = "production-request-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String RESERVED_STORAGE_ID_STRING = "reserved-storage-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ProductionRequestConverter underTest;

    @Test
    void convertDomain() {
        ProductionRequestModel model = new ProductionRequestModel();
        model.setId(PRODUCTION_REQUEST_ID);
        model.setGameId(GAME_ID);
        model.setReservedStorageId(RESERVED_STORAGE_ID);
        model.setRequestedAmount(REQUESTED_AMOUNT);
        model.setDispatchedAmount(DISPATCHED_AMOUNT);

        given(uuidConverter.convertDomain(PRODUCTION_REQUEST_ID)).willReturn(PRODUCTION_REQUEST_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(RESERVED_STORAGE_ID)).willReturn(RESERVED_STORAGE_ID_STRING);

        assertThat(underTest.convertDomain(model))
            .returns(PRODUCTION_REQUEST_ID_STRING, ProductionRequestEntity::getProductionRequestId)
            .returns(GAME_ID_STRING, ProductionRequestEntity::getGameId)
            .returns(RESERVED_STORAGE_ID_STRING, ProductionRequestEntity::getReservedStorageId)
            .returns(REQUESTED_AMOUNT, ProductionRequestEntity::getRequestedAmount)
            .returns(DISPATCHED_AMOUNT, ProductionRequestEntity::getDispatchedAmount);
    }

    @Test
    void convertEntity() {
        ProductionRequestEntity entity = ProductionRequestEntity.builder()
            .productionRequestId(PRODUCTION_REQUEST_ID_STRING)
            .gameId(GAME_ID_STRING)
            .reservedStorageId(RESERVED_STORAGE_ID_STRING)
            .requestedAmount(REQUESTED_AMOUNT)
            .dispatchedAmount(DISPATCHED_AMOUNT)
            .build();

        given(uuidConverter.convertEntity(PRODUCTION_REQUEST_ID_STRING)).willReturn(PRODUCTION_REQUEST_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(RESERVED_STORAGE_ID_STRING)).willReturn(RESERVED_STORAGE_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(PRODUCTION_REQUEST_ID, ProductionRequestModel::getId)
            .returns(GameItemType.PRODUCTION_REQUEST, GameItem::getType)
            .returns(GAME_ID, ProductionRequestModel::getGameId)
            .returns(RESERVED_STORAGE_ID, ProductionRequestModel::getReservedStorageId)
            .returns(REQUESTED_AMOUNT, ProductionRequestModel::getRequestedAmount)
            .returns(DISPATCHED_AMOUNT, ProductionRequestModel::getDispatchedAmount);
    }
}