package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
public class ConstructionConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final Integer REQUIRED_WORK_POINTS = 245;
    private static final Integer CURRENT_WORK_POINTS = 3465;
    private static final Integer PRIORITY = 5726;
    private static final String CONSTRUCTION_ID_STRING = "construction-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ConstructionConverter underTest;

    @Test
    public void convertDomain() {
        ConstructionModel model = new ConstructionModel();
        model.setId(CONSTRUCTION_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setLocationType(LOCATION_TYPE);
        model.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        model.setCurrentWorkPoints(CURRENT_WORK_POINTS);
        model.setPriority(PRIORITY);

        given(uuidConverter.convertDomain(CONSTRUCTION_ID)).willReturn(CONSTRUCTION_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        ConstructionEntity result = underTest.convertDomain(model);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }

    @Test
    public void convertEntity() {
        ConstructionEntity entity = new ConstructionEntity();
        entity.setConstructionId(CONSTRUCTION_ID_STRING);
        entity.setGameId(GAME_ID_STRING);
        entity.setLocation(LOCATION_STRING);
        entity.setLocationType(LOCATION_TYPE);
        entity.setRequiredWorkPoints(REQUIRED_WORK_POINTS);
        entity.setCurrentWorkPoints(CURRENT_WORK_POINTS);
        entity.setPriority(PRIORITY);

        given(uuidConverter.convertEntity(CONSTRUCTION_ID_STRING)).willReturn(CONSTRUCTION_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        ConstructionModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CONSTRUCTION);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getLocationType()).isEqualTo(LOCATION_TYPE);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
    }
}