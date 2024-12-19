package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.DeconstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.SurfaceConstructionAreaResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionAreaConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private ConstructionConverter constructionConverter;

    @Mock
    private DeconstructionConverter deconstructionConverter;

    @InjectMocks
    private ConstructionAreaConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private Constructions constructions;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Construction construction;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private ConstructionResponse constructionResponse;

    @Mock
    private DeconstructionResponse deconstructionResponse;

    @Test
    void convert() {
        ConstructionArea constructionArea = ConstructionArea.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID)
            .location(LOCATION)
            .surfaceId(SURFACE_ID)
            .dataId(DATA_ID)
            .build();

        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.stream()).willReturn(Stream.of(constructionArea));

        CustomAssertions.singleListAssertThat(underTest.convert(GAME_ID, gameData))
            .returns(CONSTRUCTION_AREA_ID, GameItem::getId)
            .returns(GAME_ID, GameItem::getGameId)
            .returns(GameItemType.CONSTRUCTION_AREA, GameItem::getType)
            .returns(LOCATION, ConstructionAreaModel::getLocation)
            .returns(SURFACE_ID, ConstructionAreaModel::getSurfaceId)
            .returns(DATA_ID, ConstructionAreaModel::getDataId);
    }

    @Test
    void toResponse() {
        ConstructionArea constructionArea = ConstructionArea.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID)
            .location(LOCATION)
            .surfaceId(SURFACE_ID)
            .dataId(DATA_ID)
            .build();

        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(constructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.of(construction));
        given(deconstructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.of(deconstruction));
        given(constructionConverter.toResponse(construction)).willReturn(constructionResponse);
        given(deconstructionConverter.toResponse(deconstruction)).willReturn(deconstructionResponse);

        assertThat(underTest.toResponse(gameData, constructionArea))
            .returns(CONSTRUCTION_AREA_ID, SurfaceConstructionAreaResponse::getConstructionAreaId)
            .returns(DATA_ID, SurfaceConstructionAreaResponse::getDataId)
            .returns(constructionResponse, SurfaceConstructionAreaResponse::getConstruction)
            .returns(deconstructionResponse, SurfaceConstructionAreaResponse::getDeconstruction);
    }

    @Test
    void toResponse_nulls() {
        ConstructionArea constructionArea = ConstructionArea.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID)
            .location(LOCATION)
            .surfaceId(SURFACE_ID)
            .dataId(DATA_ID)
            .build();

        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(constructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.empty());
        given(deconstructions.findByExternalReference(CONSTRUCTION_AREA_ID)).willReturn(Optional.empty());

        assertThat(underTest.toResponse(gameData, constructionArea))
            .returns(CONSTRUCTION_AREA_ID, SurfaceConstructionAreaResponse::getConstructionAreaId)
            .returns(DATA_ID, SurfaceConstructionAreaResponse::getDataId)
            .returns(null, SurfaceConstructionAreaResponse::getConstruction)
            .returns(null, SurfaceConstructionAreaResponse::getDeconstruction);
    }
}