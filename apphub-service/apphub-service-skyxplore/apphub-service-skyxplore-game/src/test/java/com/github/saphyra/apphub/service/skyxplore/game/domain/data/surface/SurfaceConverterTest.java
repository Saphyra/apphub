package com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.SurfaceConstructionAreaResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.SurfaceResponse;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreaConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SurfaceConverterTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ConstructionAreaConverter constructionAreaConverter;

    @Mock
    private ConstructionConverter constructionConverter;

    @InjectMocks
    private SurfaceConverter underTest;

    @Mock
    private Coordinates coordinates;

    @Mock
    private Coordinate coordinate;

    @Mock
    private ConstructionArea constructionArea;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private SurfaceConstructionAreaResponse constructionAreaResponse;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction terraformation;

    @Mock
    private ConstructionResponse terraformationResponse;

    @Mock
    private GameData gameData;

    @Test
    void toModel() {
        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .planetId(PLANET_ID)
            .surfaceType(SurfaceType.CONCRETE)
            .build();

        SurfaceModel result = underTest.toModel(GAME_ID, surface);

        assertThat(result.getId()).isEqualTo(SURFACE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SURFACE);
        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.CONCRETE.name());
    }

    @Test
    void toResponse() {
        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .planetId(PLANET_ID)
            .surfaceType(SurfaceType.CONCRETE)
            .build();

        given(gameData.getCoordinates()).willReturn(coordinates);
        given(coordinates.findByReferenceId(SURFACE_ID)).willReturn(coordinate);

        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findBySurfaceId(SURFACE_ID)).willReturn(Optional.of(constructionArea));
        given(constructionAreaConverter.toResponse(gameData, constructionArea)).willReturn(constructionAreaResponse);

        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(terraformation));
        given(constructionConverter.toResponse(terraformation)).willReturn(terraformationResponse);

        SurfaceResponse result = underTest.toResponse(gameData, surface);

        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.CONCRETE.name());
        assertThat(result.getConstructionArea()).isEqualTo(constructionAreaResponse);
        assertThat(result.getTerraformation()).isEqualTo(terraformationResponse);
    }
}