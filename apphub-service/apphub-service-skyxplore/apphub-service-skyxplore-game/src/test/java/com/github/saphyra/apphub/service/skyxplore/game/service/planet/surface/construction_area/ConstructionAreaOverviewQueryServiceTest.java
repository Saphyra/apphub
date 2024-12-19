package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.BuildingModuleOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionAreaOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.BuildingModuleOverviewQueryService;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionAreaOverviewQueryServiceTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String CONSTRUCTION_AREA_DATA_ID = "construction-area-data-id";
    private static final String BUILDING_MODULE_CATEGORY = "building-module-category";

    @Mock
    private BuildingModuleOverviewQueryService buildingModuleOverviewQueryService;

    @InjectMocks
    private ConstructionAreaOverviewQueryService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Surface surface;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private BuildingModuleOverviewResponse buildingModuleOverviewResponse;

    @Test
    void getConstructionAreaOverview() {
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(constructionArea));
        given(constructionArea.getDataId()).willReturn(CONSTRUCTION_AREA_DATA_ID);
        given(buildingModuleOverviewQueryService.getBuildingModuleOverview(gameData, CONSTRUCTION_AREA_DATA_ID, List.of(constructionArea))).willReturn(Map.of(BUILDING_MODULE_CATEGORY, buildingModuleOverviewResponse));

        CustomAssertions.singleListAssertThat(underTest.getConstructionAreaOverview(gameData, List.of(surface)))
            .returns(CONSTRUCTION_AREA_DATA_ID, ConstructionAreaOverviewResponse::getDataId)
            .returns(Map.of(BUILDING_MODULE_CATEGORY, buildingModuleOverviewResponse), ConstructionAreaOverviewResponse::getBuildingModules);
    }
}