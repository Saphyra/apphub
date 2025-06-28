package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResourceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StoredResourceAggregatorTest {
    private static final Integer AMOUNT_1 = 312;
    private static final Integer AMOUNT_2 = 2;
    private static final UUID STORED_RESOURCE_ID_1 = UUID.randomUUID();
    private static final UUID STORED_RESOURCE_ID_2 = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID CONTAINER_ID = UUID.randomUUID();

    @Mock
    private StoredResourceFactory storedResourceFactory;

    @InjectMocks
    private StoredResourceAggregator underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResource storedResource1;

    @Mock
    private StoredResource storedResource2;

    @Mock
    private StoredResource storedResource3;

    @Mock
    private StoredResources storedResources;

    @Test
    void aggregate_single() {
        assertThat(underTest.aggregate(progressDiff, gameData, Map.of(UUID.randomUUID(), List.of(storedResource1)))).containsExactly(storedResource1);
    }

    @Test
    void aggregate_multiple() {
        given(storedResource1.getAmount()).willReturn(AMOUNT_1);
        given(storedResource2.getAmount()).willReturn(AMOUNT_2);
        given(gameData.getStoredResources()).willReturn(storedResources);
        given(storedResource1.getStoredResourceId()).willReturn(STORED_RESOURCE_ID_1);
        given(storedResource2.getStoredResourceId()).willReturn(STORED_RESOURCE_ID_2);
        given(storedResource1.getLocation()).willReturn(LOCATION);
        given(storedResource1.getDataId()).willReturn(DATA_ID);
        given(storedResource1.getContainerId()).willReturn(CONTAINER_ID);
        given(storedResource1.getContainerType()).willReturn(ContainerType.STORAGE);
        given(storedResourceFactory.save(progressDiff, gameData, LOCATION, DATA_ID, AMOUNT_1 + AMOUNT_2, CONTAINER_ID, ContainerType.STORAGE)).willReturn(storedResource3);

        assertThat(underTest.aggregate(progressDiff, gameData, Map.of(UUID.randomUUID(), List.of(storedResource1, storedResource2)))).containsExactly(storedResource3);

        then(storedResources).should().removeAll(List.of(storedResource1, storedResource2));
        then(progressDiff).should().delete(STORED_RESOURCE_ID_1, GameItemType.STORED_RESOURCE);
        then(progressDiff).should().delete(STORED_RESOURCE_ID_2, GameItemType.STORED_RESOURCE);
    }
}