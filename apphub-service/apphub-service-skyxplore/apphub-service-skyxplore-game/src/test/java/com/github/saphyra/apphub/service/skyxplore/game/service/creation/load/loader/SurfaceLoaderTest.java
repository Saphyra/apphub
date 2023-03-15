package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceLoaderTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private CoordinateLoader coordinateLoader;

    @Mock
    private BuildingLoader buildingLoader;

    @Mock
    private ConstructionLoader constructionLoader;

    @SuppressWarnings("unused")
    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(Mockito.mock(ErrorReporterService.class));

    @InjectMocks
    private SurfaceLoader underTest;

    @Mock
    private SurfaceModel surfaceModel;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Building building;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Construction construction;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(PLANET_ID, GameItemType.SURFACE, SurfaceModel[].class)).willReturn(Arrays.asList(surfaceModel));

        given(surfaceModel.getId()).willReturn(SURFACE_ID);
        given(surfaceModel.getPlanetId()).willReturn(PLANET_ID);
        given(coordinateLoader.loadOneByReferenceId(SURFACE_ID)).willReturn(coordinateModel);
        given(surfaceModel.getSurfaceType()).willReturn(SurfaceType.CONCRETE.name());
        given(buildingLoader.load(SURFACE_ID)).willReturn(building);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);
        given(constructionLoader.load(SURFACE_ID)).willReturn(construction);

        Map<Coordinate, Surface> result = underTest.load(PLANET_ID);

        assertThat(result).hasSize(1);
        Surface surface = result.get(coordinate);
        assertThat(surface.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(surface.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(surface.getCoordinate()).isEqualTo(coordinateModel);
        assertThat(surface.getSurfaceType()).isEqualTo(SurfaceType.CONCRETE);
        assertThat(surface.getBuilding()).isEqualTo(building);
        assertThat(surface.getTerraformation()).isEqualTo(construction);
    }
}