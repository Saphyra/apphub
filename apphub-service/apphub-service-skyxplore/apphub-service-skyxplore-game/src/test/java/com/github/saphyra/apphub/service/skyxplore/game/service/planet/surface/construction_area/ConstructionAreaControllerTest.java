package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.common.CancelDeconstructionFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConstructionAreaControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CONSTRUCTION_AREA_DATA_ID = "construction-area-data-id";
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final String BUILDING_MODULE_DATA_ID = "building-module-data-id";

    @Mock
    private ConstructConstructionAreaService constructConstructionAreaService;

    @Mock
    private CancelConstructionAreaConstructionService cancelConstructionAreaConstructionService;

    @Mock
    private DeconstructConstructionAreaService deconstructConstructionAreaService;

    @Mock
    private CancelDeconstructionFacade cancelDeconstructionFacade;

    @Mock
    private AvailableBuildingModulesService availableBuildingModulesService;

    @InjectMocks
    private ConstructionAreaController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void constructConstructionArea() {
        underTest.constructConstructionArea(new OneParamRequest<>(CONSTRUCTION_AREA_DATA_ID), SURFACE_ID, accessTokenHeader);

        then(constructConstructionAreaService).should()
            .constructConstructionArea(USER_ID, SURFACE_ID, CONSTRUCTION_AREA_DATA_ID);
    }

    @Test
    void cancelConstructionAreaConstruction() {
        underTest.cancelConstructionAreaConstruction(CONSTRUCTION_ID, accessTokenHeader);

        then(cancelConstructionAreaConstructionService).should().cancelConstruction(USER_ID, CONSTRUCTION_ID);
    }

    @Test
    void deconstructConstructionArea() {
        underTest.deconstructConstructionArea(CONSTRUCTION_AREA_ID, accessTokenHeader);

        then(deconstructConstructionAreaService).should().deconstructConstructionArea(USER_ID, CONSTRUCTION_AREA_ID);
    }

    @Test
    void cancelDeconstructConstructionArea() {
        underTest.cancelDeconstructConstructionArea(DECONSTRUCTION_ID, accessTokenHeader);

        then(cancelDeconstructionFacade).should().cancelDeconstructionOfConstructionArea(USER_ID, DECONSTRUCTION_ID);
    }

    @Test
    void getAvailableBuildingModules() {
        given(availableBuildingModulesService.getAvailableBuildings(USER_ID, CONSTRUCTION_AREA_ID, BuildingModuleCategory.FARM.name())).willReturn(List.of(BUILDING_MODULE_DATA_ID));

        assertThat(underTest.getAvailableBuildingModules(CONSTRUCTION_AREA_ID, BuildingModuleCategory.FARM.name(), accessTokenHeader)).containsExactly(BUILDING_MODULE_DATA_ID);
    }
}