package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.ConstructNewBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.UpgradeBuildingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
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

    @InjectMocks
    private BuildingController underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void constructNewBuilding() {
        given(constructNewBuildingService.constructNewBuilding(USER_ID, DATA_ID, PLANET_ID, SURFACE_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.constructNewBuilding(new OneParamRequest<>(DATA_ID), PLANET_ID, SURFACE_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }

    @Test
    public void upgradeBuilding() {
        given(upgradeBuildingService.upgradeBuilding(USER_ID, PLANET_ID, BUILDING_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.upgradeBuilding(PLANET_ID, BUILDING_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }

    @Test
    public void cancelConstruction() {
        given(cancelConstructionService.cancelConstructionOfBuilding(USER_ID, PLANET_ID, BUILDING_ID)).willReturn(surfaceResponse);

        SurfaceResponse result = underTest.cancelConstruction(PLANET_ID, BUILDING_ID, accessTokenHeader);

        assertThat(result).isEqualTo(surfaceResponse);
    }
}