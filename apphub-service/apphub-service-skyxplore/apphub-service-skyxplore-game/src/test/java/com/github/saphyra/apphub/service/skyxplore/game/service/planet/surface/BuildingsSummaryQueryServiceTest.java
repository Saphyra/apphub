package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionAreaOverviewResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.ConstructionAreaOverviewQueryService;
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
class BuildingsSummaryQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private ConstructionAreaOverviewQueryService constructionAreaOverviewQueryService;

    @InjectMocks
    private BuildingsSummaryQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private ConstructionAreaOverviewResponse constructionAreaOverviewResponse;

    @Test
    void getBuildingsSummary() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.getByPlanetId(PLANET_ID)).willReturn(List.of(surface));
        given(surface.getSurfaceType()).willReturn(SurfaceType.DESERT);
        given(constructionAreaOverviewQueryService.getConstructionAreaOverview(gameData, List.of(surface))).willReturn(List.of(constructionAreaOverviewResponse));

        assertThat(underTest.getBuildingsSummary(USER_ID, PLANET_ID)).containsEntry(SurfaceType.DESERT.name(), List.of(constructionAreaOverviewResponse));
    }
}