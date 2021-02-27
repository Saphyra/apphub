package com.github.saphyra.apphub.service.skyxplore.data.save_game.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
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
public class PriorityConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String LOCATION_TYPE = "location-type";
    private static final Integer VALUE = 3252;
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final String PRIORITY_TYPE = "priority-type";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private PriorityConverter underTest;

    @Test
    public void convertDomain() {
        PriorityModel model = new PriorityModel();
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setLocationType(LOCATION_TYPE);
        model.setPriorityType(PRIORITY_TYPE);
        model.setValue(VALUE);

        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        PriorityEntity result = underTest.convertDomain(model);

        assertThat(result.getPk().getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getPk().getPriorityType()).isEqualTo(PRIORITY_TYPE);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getValue()).isEqualTo(VALUE);
    }

    @Test
    public void convertEntity() {
        PriorityEntity entity = PriorityEntity.builder()
            .pk(
                PriorityPk.builder()
                    .location(LOCATION_STRING)
                    .priorityType(PRIORITY_TYPE)
                    .build()
            )
            .gameId(GAME_ID_STRING)
            .value(VALUE)
            .locationType(LOCATION_TYPE)
            .build();

        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        PriorityModel result = underTest.convertEntity(entity);

        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getPriorityType()).isEqualTo(PRIORITY_TYPE);
        assertThat(result.getType()).isEqualTo(GameItemType.PRIORITY);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getValue()).isEqualTo(VALUE);
    }
}