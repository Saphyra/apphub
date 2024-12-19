package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AvailableBuildingModulesServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID_1 = "data-id-1";
    private static final String DATA_ID_2 = "data-id-2";

    @Mock
    private GameDao gameDao;

    @Mock
    private BuildingModuleDataService buildingModuleDataService;

    @InjectMocks
    private AvailableBuildingModulesService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private BuildingModuleData differentCategoryBuildingModuleData;

    @Mock
    private BuildingModuleData unrestrictedBuildingModuleData;

    @Mock
    private BuildingModuleData restrictedBuildingModuleData;

    @Mock
    private BuildingModuleData matchingBuildingModuleData;

    @Test
    void nullBuildingModuleCategory() {
        ExceptionValidator.validateInvalidParam(() -> underTest.getAvailableBuildings(USER_ID, CONSTRUCTION_AREA_ID, null), "buildingModuleCategory", "must not be null");
    }

    @Test
    void invalidBuildingModuleCategory() {
        ExceptionValidator.validateInvalidParam(() -> underTest.getAvailableBuildings(USER_ID, CONSTRUCTION_AREA_ID, "asd"), "buildingModuleCategory", "invalid value");
    }

    @Test
    void getAvailableBuildings() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByConstructionAreaIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceIdValidated(SURFACE_ID)).willReturn(surface);
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(buildingModuleDataService.values()).willReturn(List.of(differentCategoryBuildingModuleData, unrestrictedBuildingModuleData, restrictedBuildingModuleData, matchingBuildingModuleData));
        given(differentCategoryBuildingModuleData.getCategory()).willReturn(BuildingModuleCategory.CULTURAL);
        given(unrestrictedBuildingModuleData.getCategory()).willReturn(BuildingModuleCategory.FARM);
        given(restrictedBuildingModuleData.getCategory()).willReturn(BuildingModuleCategory.FARM);
        given(matchingBuildingModuleData.getCategory()).willReturn(BuildingModuleCategory.FARM);
        given(unrestrictedBuildingModuleData.getSupportedSurfacesRestriction()).willReturn(Collections.emptyList());
        given(restrictedBuildingModuleData.getSupportedSurfacesRestriction()).willReturn(List.of(SurfaceType.CONCRETE));
        given(matchingBuildingModuleData.getSupportedSurfacesRestriction()).willReturn(List.of(SurfaceType.DESERT));
        given(unrestrictedBuildingModuleData.getId()).willReturn(DATA_ID_1);
        given(matchingBuildingModuleData.getId()).willReturn(DATA_ID_2);

        assertThat(underTest.getAvailableBuildings(USER_ID, CONSTRUCTION_AREA_ID, BuildingModuleCategory.FARM.name())).containsExactlyInAnyOrder(DATA_ID_1, DATA_ID_2);
    }
}