package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.DeconstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceBuildingResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
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
class BuildingConverterTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int LEVEL = 4325;

    @Mock
    private ConstructionConverter constructionConverter;

    @Mock
    private DeconstructionConverter deconstructionConverter;

    @InjectMocks
    private BuildingConverter underTest;

    @Mock
    private ConstructionResponse constructionResponse;

    @Mock
    private DeconstructionResponse deconstructionResponse;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Construction construction;

    @Mock
    private Deconstruction deconstruction;

    @Test
    void toModel() {
        Building building = Building.builder()
            .buildingId(BUILDING_ID)
            .location(LOCATION)
            .surfaceId(SURFACE_ID)
            .dataId(DATA_ID)
            .level(LEVEL)
            .build();

        BuildingModel result = underTest.toModel(GAME_ID, building);

        assertThat(result.getId()).isEqualTo(BUILDING_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.BUILDING);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
    }

    @Test
    void toResponse() {
        Building building = Building.builder()
            .buildingId(BUILDING_ID)
            .location(LOCATION)
            .surfaceId(SURFACE_ID)
            .dataId(DATA_ID)
            .level(LEVEL)
            .build();

        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(construction));
        given(constructionConverter.toResponse(construction)).willReturn(constructionResponse);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_ID)).willReturn(Optional.of(deconstruction));
        given(deconstructionConverter.toResponse(deconstruction)).willReturn(deconstructionResponse);

        SurfaceBuildingResponse result = underTest.toResponse(gameData, building);

        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getConstruction()).isEqualTo(constructionResponse);
        assertThat(result.getDeconstruction()).isEqualTo(deconstructionResponse);
    }
}