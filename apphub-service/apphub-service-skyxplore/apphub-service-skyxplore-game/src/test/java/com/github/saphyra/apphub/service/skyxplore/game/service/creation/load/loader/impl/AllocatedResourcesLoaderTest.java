package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResource;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource.AllocatedResources;
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
class AllocatedResourcesLoaderTest {
    private static final UUID ALLOCATED_RESOURCE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer AMOUNT = 346;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private AllocatedResourcesLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private AllocatedResources allocatedResources;

    @Mock
    private AllocatedResource allocatedResource;

    @Mock
    private AllocatedResourceModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.ALLOCATED_RESOURCE);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(AllocatedResourceModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getAllocatedResources()).willReturn(allocatedResources);

        underTest.addToGameData(gameData, List.of(allocatedResource));

        verify(allocatedResources).addAll(List.of(allocatedResource));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(ALLOCATED_RESOURCE_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getDataId()).willReturn(DATA_ID);
        given(model.getAmount()).willReturn(AMOUNT);

        AllocatedResource result = underTest.convert(model);

        assertThat(result.getAllocatedResourceId()).isEqualTo(ALLOCATED_RESOURCE_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getAmount()).isEqualTo(AMOUNT);
    }
}