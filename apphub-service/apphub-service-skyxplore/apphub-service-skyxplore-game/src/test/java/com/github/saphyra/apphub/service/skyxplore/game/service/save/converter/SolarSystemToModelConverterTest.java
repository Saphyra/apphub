package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolarSystemToModelConverterTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final int RADIUS = 31;
    private static final String DEFAULT_NAME = "default-name";
    private static final String CUSTOM_NAME = "custom-name";

    private final SolarSystemToModelConverter underTest = new SolarSystemToModelConverter();

    @Test
    void convert() {
        SolarSystem solarSystem = SolarSystem.builder()
            .solarSystemId(SOLAR_SYSTEM_ID)
            .radius(RADIUS)
            .defaultName(DEFAULT_NAME)
            .customNames(CollectionUtils.singleValueMap(USER_ID, CUSTOM_NAME, new OptionalHashMap<>()))
            .build();

        SolarSystemModel result = underTest.convert(GAME_ID, solarSystem);

        assertThat(result.getId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SOLAR_SYSTEM);
        assertThat(result.getRadius()).isEqualTo(RADIUS);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).containsEntry(USER_ID, CUSTOM_NAME);
    }
}