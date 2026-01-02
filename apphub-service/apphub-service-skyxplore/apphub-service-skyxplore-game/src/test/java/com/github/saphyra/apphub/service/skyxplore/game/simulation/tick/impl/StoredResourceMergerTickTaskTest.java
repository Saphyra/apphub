package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StoredResourceMergerTickTaskTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String DATA_ID_1 = "data-id-1";
    private static final UUID ALLOCATED_BY = UUID.randomUUID();
    private static final UUID CONTAINER_ID = UUID.randomUUID();
    private static final Integer AMOUNT_1 = 23;
    private static final Integer AMOUNT_2 = 234;
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();

    @Mock
    private StoredResourceConverter storedResourceConverter;

    @InjectMocks
    private StoredResourceMergerTickTask underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private StoredResource zeroAmountStoredResource;

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Mock
    private StoredResource differentStoredResource;

    @Mock
    private StoredResourceModel storedResourceModel;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.STORED_RESOURCE_MERGER);
    }

    @Test
    void process() {
        StoredResources storedResources = new StoredResources();
        storedResources.add(zeroAmountStoredResource);
        storedResources.add(storedResource1);
        storedResources.add(storedResource2);
        storedResources.add(differentStoredResource);

        given(game.getData()).willReturn(gameData);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getGameId()).willReturn(GAME_ID);

        given(zeroAmountStoredResource.getAmount()).willReturn(0);

        given(storedResource1.getAmount()).willReturn(AMOUNT_1);
        given(storedResource1.getDataId()).willReturn(DATA_ID_1);
        given(storedResource1.getAllocatedBy()).willReturn(ALLOCATED_BY);
        given(storedResource1.getContainerId()).willReturn(CONTAINER_ID);

        given(storedResource2.getAmount()).willReturn(AMOUNT_2);
        given(storedResource2.getDataId()).willReturn(DATA_ID_1);
        given(storedResource2.getAllocatedBy()).willReturn(ALLOCATED_BY);
        given(storedResource2.getContainerId()).willReturn(CONTAINER_ID);
        given(storedResource2.getStoredResourceId()).willReturn(STORED_RESOURCE_ID);

        given(storedResourceConverter.toModel(GAME_ID, storedResource1)).willReturn(storedResourceModel);

        underTest.process(game);

        then(storedResource1).should().increaseAmount(AMOUNT_2);
        then(progressDiff).should().delete(STORED_RESOURCE_ID, GameItemType.STORED_RESOURCE);
        then(progressDiff).should().save(storedResourceModel);

        assertThat(storedResources)
            .hasSize(3)
            .doesNotContain(storedResource2);

    }
}