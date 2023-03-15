package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SolarSystemToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final int RADIUS = 235;
    private static final String DEFAULT_NAME = "default-name";
    private static final OptionalMap<UUID, String> CUSTOM_NAMES = new OptionalHashMap<>(CollectionUtils.singleValueMap(UUID.randomUUID(), "custom-name"));

    @Mock
    private PlanetToModelConverter planetToModelConverter;

    @InjectMocks
    private SolarSystemToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private PlanetModel planetModel;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void convertDeep() {
        given(game.getGameId()).willReturn(GAME_ID);

        SolarSystem solarSystem = SolarSystem.builder()
            .solarSystemId(SOLAR_SYSTEM_ID)
            .radius(RADIUS)
            .defaultName(DEFAULT_NAME)
            .customNames(CUSTOM_NAMES)
            .coordinate(coordinateModel)
            .planets(CollectionUtils.singleValueMap(UUID.randomUUID(), planet))
            .build();

        given(planetToModelConverter.convertDeep(any(), eq(game))).willReturn(Arrays.asList(planetModel));

        List<GameItem> result = underTest.convertDeep(Arrays.asList(solarSystem), game);

        SolarSystemModel expected = new SolarSystemModel();
        expected.setId(SOLAR_SYSTEM_ID);
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.SOLAR_SYSTEM);
        expected.setRadius(RADIUS);
        expected.setDefaultName(DEFAULT_NAME);
        expected.setCustomNames(CUSTOM_NAMES);

        assertThat(result).containsExactlyInAnyOrder(expected, planetModel, coordinateModel);
    }
}