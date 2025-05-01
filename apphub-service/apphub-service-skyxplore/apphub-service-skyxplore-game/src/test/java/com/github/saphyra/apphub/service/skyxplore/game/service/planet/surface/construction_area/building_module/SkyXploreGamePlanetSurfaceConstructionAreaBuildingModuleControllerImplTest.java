package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.building.BuildingModuleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
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
class SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String BUILDING_MODULE_DATA_ID = "building-module-data-id";
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private BuildingModuleQueryService buildingModuleQueryService;

    @Mock
    private ConstructBuildingModuleService constructBuildingModuleService;

    @Mock
    private CancelConstructionOfBuildingModuleService cancelConstructionOfBuildingModuleService;

    @Mock
    private DeconstructBuildingModuleService deconstructBuildingModuleService;

    @Mock
    private CancelDeconstructionFacade cancelDeconstructionFacade;

    @InjectMocks
    private SkyXploreGamePlanetSurfaceConstructionAreaBuildingModuleControllerImpl underTest;

    @Mock
    private BuildingModuleResponse buildingModuleResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(buildingModuleQueryService.getBuildingModulesOfConstructionArea(USER_ID, CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModuleResponse));
    }

    @Test
    void getBuildingModules() {
        assertThat(underTest.getBuildingModules(CONSTRUCTION_AREA_ID, accessTokenHeader)).containsExactly(buildingModuleResponse);
    }

    @Test
    void constructBuildingModule() {
        assertThat(underTest.constructBuildingModule(new OneParamRequest<>(BUILDING_MODULE_DATA_ID), CONSTRUCTION_AREA_ID, accessTokenHeader)).containsExactly(buildingModuleResponse);

        then(constructBuildingModuleService).should().constructBuildingModule(USER_ID, CONSTRUCTION_AREA_ID, BUILDING_MODULE_DATA_ID);
    }

    @Test
    void cancelConstructionOfBuildingModule() {
        given(cancelConstructionOfBuildingModuleService.cancelConstruction(USER_ID, CONSTRUCTION_ID)).willReturn(CONSTRUCTION_AREA_ID);

        assertThat(underTest.cancelConstructionOfBuildingModule(CONSTRUCTION_ID, accessTokenHeader)).containsExactly(buildingModuleResponse);
    }

    @Test
    void deconstructBuildingModule() {
        given(deconstructBuildingModuleService.deconstructBuildingModule(USER_ID, BUILDING_MODULE_ID)).willReturn(CONSTRUCTION_AREA_ID);

        assertThat(underTest.deconstructBuildingModule(BUILDING_MODULE_ID, accessTokenHeader)).containsExactly(buildingModuleResponse);
    }

    @Test
    void cancelDeconstructionOfBuildingModule() {
        given(cancelDeconstructionFacade.cancelDeconstructionOfBuildingModule(USER_ID, DECONSTRUCTION_ID)).willReturn(CONSTRUCTION_AREA_ID);

        assertThat(underTest.cancelDeconstructionOfBuildingModule(DECONSTRUCTION_ID, accessTokenHeader)).containsExactly(buildingModuleResponse);
    }
}