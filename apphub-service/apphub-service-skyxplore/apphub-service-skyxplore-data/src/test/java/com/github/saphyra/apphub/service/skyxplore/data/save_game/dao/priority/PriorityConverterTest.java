package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
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
public class PriorityConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final Integer VALUE = 3252;
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final String PRIORITY_TYPE = "priority-type";
    private static final UUID PRIORITY_ID = UUID.randomUUID();
    private static final String PRIORITY_ID_STRING = "priority-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private PriorityConverter underTest;

    @Test
    public void convertDomain() {
        PriorityModel model = new PriorityModel();
        model.setId(PRIORITY_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setPriorityType(PRIORITY_TYPE);
        model.setValue(VALUE);

        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);
        given(uuidConverter.convertDomain(PRIORITY_ID)).willReturn(PRIORITY_ID_STRING);

        PriorityEntity result = underTest.convertDomain(model);

        assertThat(result.getPriorityId()).isEqualTo(PRIORITY_ID_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getPriorityType()).isEqualTo(PRIORITY_TYPE);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getValue()).isEqualTo(VALUE);
    }

    @Test
    public void convertEntity() {
        PriorityEntity entity = PriorityEntity.builder()
            .priorityId(PRIORITY_ID_STRING)
            .priorityType(PRIORITY_TYPE)
            .gameId(GAME_ID_STRING)
            .location(LOCATION_STRING)
            .value(VALUE)
            .build();

        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);
        given(uuidConverter.convertEntity(PRIORITY_ID_STRING)).willReturn(PRIORITY_ID);

        PriorityModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(PRIORITY_ID);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getPriorityType()).isEqualTo(PRIORITY_TYPE);
        assertThat(result.getType()).isEqualTo(GameItemType.PRIORITY);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getValue()).isEqualTo(VALUE);
    }
}