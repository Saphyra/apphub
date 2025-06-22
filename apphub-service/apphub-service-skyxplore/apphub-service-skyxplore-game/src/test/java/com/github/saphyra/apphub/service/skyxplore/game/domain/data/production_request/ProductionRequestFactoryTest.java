package com.github.saphyra.apphub.service.skyxplore.game.domain.data.production_request;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProductionRequestModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductionRequestFactoryTest {
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();
    private static final int REQUESTED_AMOUNT = 24;
    private static final UUID PRODUCTION_REQUEST_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ProductionRequestConverter productionRequestConverter;

    @InjectMocks
    private ProductionRequestFactory underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private ProductionRequestModel model;

    @Mock
    private ProductionRequests productionRequests;

    @Test
    void save() {
        given(idGenerator.randomUuid()).willReturn(PRODUCTION_REQUEST_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(gameData.getProductionRequests()).willReturn(productionRequests);
        given(productionRequestConverter.toModel(eq(GAME_ID), any())).willReturn(model);

        ProductionRequest result = underTest.save(progressDiff, gameData, RESERVED_STORAGE_ID, REQUESTED_AMOUNT);

        then(productionRequests).should().add(result);
        then(progressDiff).should().save(model);

        assertThat(result)
            .returns(PRODUCTION_REQUEST_ID, ProductionRequest::getProductionRequestId)
            .returns(RESERVED_STORAGE_ID, ProductionRequest::getReservedStorageId)
            .returns(REQUESTED_AMOUNT, ProductionRequest::getRequestedAmount)
            .returns(0, ProductionRequest::getDispatchedAmount);
    }
}