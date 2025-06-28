package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocations;
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
class BuildingModuleAllocationLoaderTest {
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private BuildingModuleAllocationLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModuleAllocations buildingModuleAllocations;

    @Mock
    private BuildingModuleAllocation buildingModuleAllocation;

    @Mock
    private BuildingModuleAllocationModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.BUILDING_MODULE_ALLOCATION);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(BuildingModuleAllocationModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);

        underTest.addToGameData(gameData, List.of(buildingModuleAllocation));

        verify(buildingModuleAllocations).addAll(List.of(buildingModuleAllocation));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(BUILDING_ALLOCATION_ID);
        given(model.getBuildingModuleId()).willReturn(BUILDING_ID);
        given(model.getProcessId()).willReturn(PROCESS_ID);

        BuildingModuleAllocation result = underTest.convert(model);

        assertThat(result.getBuildingModuleAllocationId()).isEqualTo(BUILDING_ALLOCATION_ID);
        assertThat(result.getBuildingModuleId()).isEqualTo(BUILDING_ID);
        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
    }
}