package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.ConstructNewBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.UpgradeBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.CancelDeconstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct.DeconstructBuildingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BuildingControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private ConstructNewBuildingService constructNewBuildingService;

    @Mock
    private UpgradeBuildingService upgradeBuildingService;

    @Mock
    private CancelConstructionService cancelConstructionService;

    @Mock
    private DeconstructBuildingService deconstructBuildingService;

    @Mock
    private CancelDeconstructionService cancelDeconstructionService;

    @InjectMocks
    private BuildingController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SurfaceResponse surfaceResponse;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void constructNewBuilding() {
        given(constructNewBuildingService.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.constructNewBuilding(new OneParamRequest<>(DATA_ID), PLANET_ID, SURFACE_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }

    @Test
    void upgradeBuilding() {
        given(upgradeBuildingService.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.upgradeBuilding(PLANET_ID, BUILDING_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }

    @Test
    void cancelConstruction() {
        given(cancelConstructionService.cancelConstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.cancelConstruction(PLANET_ID, BUILDING_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }

    @Test
    void deconstructBuilding() {
        given(deconstructBuildingService.deconstructBuilding(USER_ID, PLANET_ID, BUILDING_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.deconstructBuilding(PLANET_ID, BUILDING_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }

    @Test
    void cancelDeconstruction() {
        given(cancelDeconstructionService.cancelDeconstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.cancelDeconstruction(PLANET_ID, BUILDING_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }
}