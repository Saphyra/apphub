package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BuildingToModelConverterTest {
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int LEVEL = 3214;
    private static final UUID GAME_ID = UUID.randomUUID();

    @InjectMocks
    private BuildingToModelConverter underTest;

    @Test
    public void convert() {
        Building building = Building.builder()
            .buildingId(BUILDING_ID)
            .dataId(DATA_ID)
            .level(LEVEL)
            .surfaceId(SURFACE_ID)
            .build();

        BuildingModel result = underTest.convert(building, GAME_ID);

        assertThat(result.getId()).isEqualTo(BUILDING_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.BUILDING);
        assertThat(result.getDataId()).isEqualTo(DATA_ID);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
    }
}