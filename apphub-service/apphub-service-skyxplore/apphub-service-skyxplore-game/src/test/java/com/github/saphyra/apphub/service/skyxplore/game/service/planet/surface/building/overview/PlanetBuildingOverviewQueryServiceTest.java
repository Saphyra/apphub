package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlanetBuildingOverviewQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private PlanetBuildingOverviewMapper overviewMapper;

    @InjectMocks
    private PlanetBuildingOverviewQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Coordinate coordinate;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Test
    public void getBuildingOverview() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.toMap(coordinate, surface)));

        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(overviewMapper.createOverview(Arrays.asList(surface))).willReturn(planetBuildingOverviewResponse);

        Map<String, PlanetBuildingOverviewResponse> result = underTest.getBuildingOverview(USER_ID, PLANET_ID);

        assertThat(result).containsEntry(SurfaceType.CONCRETE.name(), planetBuildingOverviewResponse);
    }
}