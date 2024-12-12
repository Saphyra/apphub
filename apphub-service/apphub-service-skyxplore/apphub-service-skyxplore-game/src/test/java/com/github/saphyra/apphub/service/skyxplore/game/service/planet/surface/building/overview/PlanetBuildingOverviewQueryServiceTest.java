package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
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
    private GameData gameData;

    @Mock
    private Surface surface;

    @Mock
    private Surfaces surfaces;

    @Mock
    private PlanetBuildingOverviewResponse planetBuildingOverviewResponse;

    @Test
    public void getBuildingOverview() {
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.getByPlanetId(PLANET_ID)).willReturn(List.of(surface));
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);

        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(overviewMapper.createOverview(gameData, PLANET_ID, SurfaceType.CONCRETE)).willReturn(planetBuildingOverviewResponse);

        Map<String, PlanetBuildingOverviewResponse> result = underTest.getBuildingOverview(USER_ID, PLANET_ID);

        assertThat(result).containsEntry(SurfaceType.CONCRETE.name(), planetBuildingOverviewResponse);
    }
}