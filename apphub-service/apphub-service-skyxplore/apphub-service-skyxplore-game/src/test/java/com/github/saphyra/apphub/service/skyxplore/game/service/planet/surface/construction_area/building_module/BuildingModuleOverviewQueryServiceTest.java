package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.BuildingModuleOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
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

@ExtendWith(MockitoExtension.class)
class BuildingModuleOverviewQueryServiceTest {
    private static final String CONSTRUCTION_AREA_DATA_ID = "construction-area-data-id";
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String BUILDING_MODULE_DATA_ID = "building-module-data-id";
    private static final Integer SLOT_AMOUNT = 234;

    @Mock
    private ConstructionAreaDataService constructionAreaDataService;

    @InjectMocks
    private BuildingModuleOverviewQueryService underTest;

    @Mock
    private ConstructionAreaData constructionAreaData;

    @Mock
    private GameData gameData;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Test
    void getBuildingModuleOverview() {
        given(constructionAreaDataService.get(CONSTRUCTION_AREA_DATA_ID)).willReturn(constructionAreaData);
        given(constructionAreaData.getSlots()).willReturn(Map.of(BuildingModuleCategory.DOCK, SLOT_AMOUNT));
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(constructionArea.getConstructionAreaId()).willReturn(CONSTRUCTION_AREA_ID);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getDataId()).willReturn(BUILDING_MODULE_DATA_ID);

        assertThat(underTest.getBuildingModuleOverview(gameData, CONSTRUCTION_AREA_DATA_ID, List.of(constructionArea)))
            .containsKey(BuildingModuleCategory.DOCK.name())
            .extracting(map -> map.get(BuildingModuleCategory.DOCK.name()))
            .returns(SLOT_AMOUNT, BuildingModuleOverviewResponse::getAvailableSlots)
            .returns(1, BuildingModuleOverviewResponse::getUsedSlots)
            .extracting(BuildingModuleOverviewResponse::getModules)
            .isEqualTo(Map.of(BUILDING_MODULE_DATA_ID, 1));
    }
}