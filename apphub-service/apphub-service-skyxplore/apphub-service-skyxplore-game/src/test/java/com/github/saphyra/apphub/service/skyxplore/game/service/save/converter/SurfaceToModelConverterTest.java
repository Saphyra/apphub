package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SurfaceToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Coordinate COORDINATE = new Coordinate(0, 0);

    @Mock
    private BuildingToModelConverter buildingToModelConverter;

    @InjectMocks
    private SurfaceToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Building building;

    @Mock
    private BuildingModel buildingModel;

    @Test
    public void convertDeep() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(buildingToModelConverter.convert(building, game)).willReturn(buildingModel);

        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .planetId(PLANET_ID)
            .coordinate(COORDINATE)
            .surfaceType(SurfaceType.DESERT)
            .building(building)
            .build();

        List<GameItem> result = underTest.convertDeep(Arrays.asList(surface), game);

        SurfaceModel expected = new SurfaceModel();
        expected.setId(SURFACE_ID);
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.SURFACE);
        expected.setPlanetId(PLANET_ID);
        expected.setSurfaceType(SurfaceType.DESERT.name());
        expected.setCoordinate(COORDINATE);

        assertThat(result).containsExactlyInAnyOrder(expected, buildingModel);
    }
}