package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.UuidStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.data.common.CoordinateConverter;
import com.github.saphyra.apphub.service.skyxplore.data.common.CoordinateEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SolarSystemConverterTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer RADIUS = 356;
    private static final String DEFAULT_NAME = "default-name";
    private static final UuidStringMap CUSTOM_NAMES = new UuidStringMap(CollectionUtils.singleValueMap(UUID.randomUUID(), "+af"));
    private static final String SOLAR_SYSTEM_ID_STRING = "solar-system-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String CUSTOM_NAMES_STRING = "custom-names";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CoordinateConverter coordinateConverter;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private SolarSystemConverter underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private CoordinateEntity coordinateEntity;

    @Test
    public void convertDomain() {
        SolarSystemModel model = new SolarSystemModel();
        model.setId(SOLAR_SYSTEM_ID);
        model.setGameId(GAME_ID);
        model.setRadius(RADIUS);
        model.setDefaultName(DEFAULT_NAME);
        model.setCustomNames(CUSTOM_NAMES);
        model.setCoordinate(coordinate);

        given(uuidConverter.convertDomain(SOLAR_SYSTEM_ID)).willReturn(SOLAR_SYSTEM_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(objectMapperWrapper.writeValueAsString(CUSTOM_NAMES)).willReturn(CUSTOM_NAMES_STRING);
        given(coordinateConverter.convertDomain(coordinate, SOLAR_SYSTEM_ID)).willReturn(coordinateEntity);

        SolarSystemEntity result = underTest.convertDomain(model);

        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).isEqualTo(CUSTOM_NAMES_STRING);
        assertThat(result.getCoordinate()).isEqualTo(coordinateEntity);
    }

    @Test
    public void convertEntity() {
        SolarSystemEntity entity = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_STRING)
            .gameId(GAME_ID_STRING)
            .radius(RADIUS)
            .defaultName(DEFAULT_NAME)
            .customNames(CUSTOM_NAMES_STRING)
            .coordinate(coordinateEntity)
            .build();

        given(uuidConverter.convertEntity(SOLAR_SYSTEM_ID_STRING)).willReturn(SOLAR_SYSTEM_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(objectMapperWrapper.readValue(CUSTOM_NAMES_STRING, UuidStringMap.class)).willReturn(CUSTOM_NAMES);
        given(coordinateConverter.convertEntity(coordinateEntity)).willReturn(coordinate);

        SolarSystemModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SOLAR_SYSTEM);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).isEqualTo(CUSTOM_NAMES);
        assertThat(result.getCoordinate()).isEqualTo(coordinate);
    }
}