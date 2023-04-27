package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocations;
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
class BuildingAllocationLoaderTest {
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private BuildingAllocationLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingAllocations buildingAllocations;

    @Mock
    private BuildingAllocation buildingAllocation;

    @Mock
    private BuildingAllocationModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.BUILDING_ALLOCATION);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(BuildingAllocationModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getBuildingAllocations()).willReturn(buildingAllocations);

        underTest.addToGameData(gameData, List.of(buildingAllocation));

        verify(buildingAllocations).addAll(List.of(buildingAllocation));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(BUILDING_ALLOCATION_ID);
        given(model.getBuildingId()).willReturn(BUILDING_ID);
        given(model.getProcessId()).willReturn(PROCESS_ID);

        BuildingAllocation result = underTest.convert(model);

        assertThat(result.getBuildingAllocationId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}