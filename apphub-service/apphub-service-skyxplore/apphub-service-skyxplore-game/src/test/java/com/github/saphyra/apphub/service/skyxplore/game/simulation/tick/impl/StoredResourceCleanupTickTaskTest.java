package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.convoy.Convoys;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
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
class StoredResourceCleanupTickTaskTest {
    private static final UUID CONVOY_ID = UUID.randomUUID();
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();

    @InjectMocks
    private StoredResourceCleanupTickTask underTest;

    @Mock
    private Game game;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Convoy convoy;

    @Mock
    private StoredResource allocatedStoredResource;

    @Mock
    private StoredResource convoyStoredResource;

    @Mock
    private StoredResource toDeleteStoredResource;

    @Mock
    private GameData gameData;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.STORED_RESOURCE_CLEANUP);
    }

    @Test
    void process() {
        Convoys convoys = new Convoys();
        convoys.add(convoy);

        StoredResources storedResources = new StoredResources();
        storedResources.add(allocatedStoredResource);
        storedResources.add(convoyStoredResource);
        storedResources.add(toDeleteStoredResource);

        given(game.getData()).willReturn(gameData);
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(gameData.getConvoys()).willReturn(convoys);
        given(gameData.getStoredResources()).willReturn(storedResources);

        given(convoy.getConvoyId()).willReturn(CONVOY_ID);
        given(convoyStoredResource.getContainerId()).willReturn(CONVOY_ID);
        given(allocatedStoredResource.getAllocatedBy()).willReturn(UUID.randomUUID());
        given(toDeleteStoredResource.getStoredResourceId()).willReturn(STORED_RESOURCE_ID);

        underTest.process(game);

        then(progressDiff).should().delete(STORED_RESOURCE_ID, GameItemType.STORED_RESOURCE);
        assertThat(storedResources)
            .hasSize(2)
            .doesNotContain(toDeleteStoredResource);
    }
}