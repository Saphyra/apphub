package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BuildingConverterTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 25342;
    private static final String BUILDING_ID_STRING = "building-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String SURFACE_ID_STRING = "surface-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BuildingConverter underTest;

    @Test
    public void convertDomain() {
        BuildingModel model = new BuildingModel();
        model.setId(BUILDING_ID);
        model.setGameId(GAME_ID);
        model.setSurfaceId(SURFACE_ID);
        model.setDataId(DATA_ID);
        model.setLevel(LEVEL);

        given(uuidConverter.convertDomain(BUILDING_ID)).willReturn(BUILDING_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(SURFACE_ID)).willReturn(SURFACE_ID_STRING);

        BuildingEntity result = underTest.convertDomain(model);

        assertThat(result.getBuildingId()).isEqualTo(BUILDING_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID_STRING);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
    }

    @Test
    public void convertEntity() {
        BuildingEntity entity = BuildingEntity.builder()
            .buildingId(BUILDING_ID_STRING)
            .gameId(GAME_ID_STRING)
            .surfaceId(SURFACE_ID_STRING)
            .dataId(DATA_ID)
            .level(LEVEL)
            .build();

        given(uuidConverter.convertEntity(BUILDING_ID_STRING)).willReturn(BUILDING_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(SURFACE_ID_STRING)).willReturn(SURFACE_ID);

        BuildingModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(BUILDING_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.BUILDING);
        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
    }
}