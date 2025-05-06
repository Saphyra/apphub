package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StoredResourceModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource.StoredResources;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoredResourceLoaderTest {
    private static final UUID STORED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 256;
    private static final UUID CONTAINER_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private StoredResourceLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private StoredResources storedResources;

    @Mock
    private StoredResource storedResource;

    @Mock
    private StoredResourceModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.STORED_RESOURCE);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(StoredResourceModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getStoredResources()).willReturn(storedResources);

        underTest.addToGameData(gameData, List.of(storedResource));

        verify(storedResources).addAll(List.of(storedResource));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(STORED_RESOURCE_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getAmount()).willReturn(AMOUNT);
        given(model.getContainerId()).willReturn(CONTAINER_ID);
        given(model.getContainerType()).willReturn(ContainerType.SURFACE);

        StoredResource result = underTest.convert(model);

        assertThat(result.getStoredResourceId()).isEqualTo(STORED_RESOURCE_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getContainerId()).isEqualTo(CONTAINER_ID);
        assertThat(result.getContainerType()).isEqualTo(ContainerType.SURFACE);
    }
}