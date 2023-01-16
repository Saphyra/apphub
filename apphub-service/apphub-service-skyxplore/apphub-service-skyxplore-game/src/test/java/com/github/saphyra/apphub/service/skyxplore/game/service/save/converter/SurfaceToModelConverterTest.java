package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private BuildingToModelConverter buildingToModelConverter;

    @Mock
    private ConstructionToModelConverter constructionToModelConverter;

    @InjectMocks
    private SurfaceToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Building building;

    @Mock
    private BuildingModel buildingModel;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionModel constructionModel;

    @Test
    public void convertDeep() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(buildingToModelConverter.convert(building, GAME_ID)).willReturn(buildingModel);
        given(constructionToModelConverter.convert(construction, GAME_ID)).willReturn(constructionModel);

        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .planetId(PLANET_ID)
            .coordinate(coordinateModel)
            .surfaceType(SurfaceType.DESERT)
            .building(building)
            .terraformation(construction)
            .build();

        List<GameItem> result = underTest.convertDeep(Arrays.asList(surface), game);

        SurfaceModel expected = new SurfaceModel();
        expected.setId(SURFACE_ID);
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.SURFACE);
        expected.setPlanetId(PLANET_ID);
        expected.setSurfaceType(SurfaceType.DESERT.name());

        assertThat(result).containsExactlyInAnyOrder(expected, buildingModel, coordinateModel, constructionModel);
    }
}