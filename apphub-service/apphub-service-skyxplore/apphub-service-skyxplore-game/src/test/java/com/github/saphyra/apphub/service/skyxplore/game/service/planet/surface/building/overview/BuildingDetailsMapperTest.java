package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewDetailedResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingDetailsMapperTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private BuildingDetailMapper buildingDetailMapper;

    @InjectMocks
    private BuildingDetailsMapper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private PlanetBuildingOverviewDetailedResponse response;

    @Mock
    private Buildings buildings;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Building building;

    @Mock
    private Surface surface;

    @Test
    void createBuildingDetails() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.getByLocation(PLANET_ID)).willReturn(List.of(building));
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(building.getDataId()).willReturn(DATA_ID);
        given(buildingDetailMapper.createBuildingDetail(gameData, DATA_ID, List.of(building))).willReturn(response);

        List<PlanetBuildingOverviewDetailedResponse> result = underTest.createBuildingDetails(gameData, PLANET_ID, SurfaceType.CONCRETE);

        assertThat(result).containsExactly(response);
    }
}