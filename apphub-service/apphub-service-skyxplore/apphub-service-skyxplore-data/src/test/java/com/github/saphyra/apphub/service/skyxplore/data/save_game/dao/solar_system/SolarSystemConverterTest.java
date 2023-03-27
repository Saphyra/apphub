package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.UuidStringMap;
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
public class SolarSystemConverterTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer RADIUS = 356;
    private static final String DEFAULT_NAME = "default-name";
    private static final UuidStringMap CUSTOM_NAMES = new UuidStringMap(CollectionUtils.toMap(UUID.randomUUID(), "+af"));
    private static final String SOLAR_SYSTEM_ID_STRING = "solar-system-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String CUSTOM_NAMES_STRING = "custom-names";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private SolarSystemConverter underTest;

    @Test
    public void convertDomain() {
        SolarSystemModel model = new SolarSystemModel();
        model.setId(SOLAR_SYSTEM_ID);
        model.setGameId(GAME_ID);
        model.setRadius(RADIUS);
        model.setDefaultName(DEFAULT_NAME);
        model.setCustomNames(CUSTOM_NAMES);

        given(uuidConverter.convertDomain(SOLAR_SYSTEM_ID)).willReturn(SOLAR_SYSTEM_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(objectMapperWrapper.writeValueAsString(CUSTOM_NAMES)).willReturn(CUSTOM_NAMES_STRING);

        SolarSystemEntity result = underTest.convertDomain(model);

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).isEqualTo(CUSTOM_NAMES_STRING);
    }

    @Test
    public void convertEntity() {
        SolarSystemEntity entity = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_STRING)
            .gameId(GAME_ID_STRING)
            .radius(RADIUS)
            .defaultName(DEFAULT_NAME)
            .customNames(CUSTOM_NAMES_STRING)
            .build();

        given(uuidConverter.convertEntity(SOLAR_SYSTEM_ID_STRING)).willReturn(SOLAR_SYSTEM_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(objectMapperWrapper.readValue(CUSTOM_NAMES_STRING, UuidStringMap.class)).willReturn(CUSTOM_NAMES);

        SolarSystemModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SOLAR_SYSTEM);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).isEqualTo(CUSTOM_NAMES);
    }
}