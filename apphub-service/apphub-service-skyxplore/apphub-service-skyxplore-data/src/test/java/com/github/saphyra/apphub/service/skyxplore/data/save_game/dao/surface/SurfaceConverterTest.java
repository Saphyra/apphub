package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SurfaceConverterTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String SURFACE_TYPE = "surface-type";
    private static final String SURFACE_ID_STRING = "surface-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String PLANET_ID_STRING = "planet-id";

    @Mock
    private UuidConverter uuidConverter;


    @InjectMocks
    private SurfaceConverter underTest;

    @Test
    public void convertDomain() {
        SurfaceModel model = new SurfaceModel();
        model.setId(SURFACE_ID);
        model.setGameId(GAME_ID);
        model.setPlanetId(PLANET_ID);
        model.setSurfaceType(SURFACE_TYPE);

        given(uuidConverter.convertDomain(SURFACE_ID)).willReturn(SURFACE_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(PLANET_ID)).willReturn(PLANET_ID_STRING);

        SurfaceEntity result = underTest.convertDomain(model);

        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID_STRING);
        assertThat(result.getSurfaceType()).isEqualTo(SURFACE_TYPE);
    }

    @Test
    public void convertEntity() {
        SurfaceEntity entity = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_STRING)
            .gameId(GAME_ID_STRING)
            .planetId(PLANET_ID_STRING)
            .surfaceType(SURFACE_TYPE)
            .build();

        given(uuidConverter.convertEntity(SURFACE_ID_STRING)).willReturn(SURFACE_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(PLANET_ID_STRING)).willReturn(PLANET_ID);

        SurfaceModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(SURFACE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getProcessType()).isEqualTo(GameItemType.SURFACE);
        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSurfaceType()).isEqualTo(SURFACE_TYPE);
    }
}