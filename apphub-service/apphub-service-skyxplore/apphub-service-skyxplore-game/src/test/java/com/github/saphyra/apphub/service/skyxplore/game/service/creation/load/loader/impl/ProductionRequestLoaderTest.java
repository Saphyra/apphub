package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request.ProductionRequests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionRequestLoaderTest {
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final Integer REQUESTED_AMOUNT = 32;
    private static final Integer DISPATCHED_AMOUNT = 245;

    @InjectMocks
    private ProductionRequestLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionRequests productionRequests;

    @Mock
    private ProductionRequest productionRequest;

    @Mock
    private ProductionRequestModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.PRODUCTION_REQUEST);
    }

    @Test
    void addToGameData() {
        given(gameData.getProductionRequests()).willReturn(productionRequests);

        underTest.addToGameData(gameData, List.of(productionRequest));

        then(productionRequests).should().addAll(List.of(productionRequest));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(PRODUCTION_REQUEST_ID);
        given(model.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);
        given(model.getRequestedAmount()).willReturn(REQUESTED_AMOUNT);
        given(model.getDispatchedAmount()).willReturn(DISPATCHED_AMOUNT);

        assertThat(underTest.convert(model))
            .returns(PRODUCTION_REQUEST_ID, ProductionRequest::getProductionRequestId)
            .returns(RESERVED_STORAGE_ID, ProductionRequest::getReservedStorageId)
            .returns(REQUESTED_AMOUNT, ProductionRequest::getRequestedAmount)
            .returns(DISPATCHED_AMOUNT, ProductionRequest::getDispatchedAmount);
    }
}