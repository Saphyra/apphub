package com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConvoyModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.logistics.ConvoyCapacityCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.logistics.RouteCalculator;
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
class ConvoyFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID RESOURCE_DELIVERY_REQUEST_ID = UUID.randomUUID();
    private static final int REQUESTED_CAPACITY = 4321;
    private static final Integer CAPACITY = 31;
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ConvoyConverter convoyConverter;

    @Mock
    private ConvoyCapacityCalculator convoyCapacityCalculator;

    @Mock
    private RouteCalculator routeCalculator;

    @InjectMocks
    private ConvoyFactory underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Convoys convoys;

    @Mock
    private ConvoyModel model;

    @Test
    void save() {
        given(convoyCapacityCalculator.calculate(gameData, LOCATION, REQUESTED_CAPACITY)).willReturn(CAPACITY);
        given(idGenerator.randomUuid()).willReturn(CONVOY_ID);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(gameData.getConvoys()).willReturn(convoys);
        given(convoyConverter.toModel(eq(GAME_ID), any(Convoy.class))).willReturn(model);

        Convoy result = underTest.save(progressDiff, gameData, LOCATION, RESOURCE_DELIVERY_REQUEST_ID, REQUESTED_CAPACITY);

        assertThat(result)
            .returns(CONVOY_ID, Convoy::getConvoyId)
            .returns(RESOURCE_DELIVERY_REQUEST_ID, Convoy::getResourceDeliveryRequestId)
            .returns(CAPACITY, Convoy::getCapacity);

        then(routeCalculator).should().calculateAndSaveForResourceDeliveryRequestId(progressDiff, gameData, LOCATION, CONVOY_ID, RESOURCE_DELIVERY_REQUEST_ID);
        then(progressDiff).should().save(model);
        then(convoys).should().add(result);
    }
}