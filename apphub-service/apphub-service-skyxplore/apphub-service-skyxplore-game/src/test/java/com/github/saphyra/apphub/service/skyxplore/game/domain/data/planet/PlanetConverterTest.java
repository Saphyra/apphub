package com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlanetConverterTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final String CUSTOM_NAME = "custom-name";
    private static final Double ORBIT_RADIUS = 2435d;
    private static final Double ORBIT_SPEED = 123d;
    private static final Integer SIZE = 32;
    private static final UUID GAME_ID = UUID.randomUUID();

    private final PlanetConverter underTest = new PlanetConverter();

    @Test
    void convert() {
        Planet planet = Planet.builder()
            .planetId(PLANET_ID)
            .solarSystemId(SOLAR_SYSTEM_ID)
            .defaultName(DEFAULT_NAME)
            .customNames(CollectionUtils.singleValueMap(USER_ID, CUSTOM_NAME, new OptionalHashMap<>()))
            .orbitRadius(ORBIT_RADIUS)
            .orbitSpeed(ORBIT_SPEED)
            .size(SIZE)
            .owner(OWNER)
            .build();

        PlanetModel result = underTest.toModel(GAME_ID, planet);

        assertThat(result.getId()).isEqualTo(PLANET_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PLANET);
        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).containsEntry(USER_ID, CUSTOM_NAME);
        assertThat(result.getOrbitRadius()).isEqualTo(ORBIT_RADIUS);
        assertThat(result.getOrbitSpeed()).isEqualTo(ORBIT_SPEED);
        assertThat(result.getSize()).isEqualTo(SIZE);
        assertThat(result.getOwner()).isEqualTo(OWNER);
    }
}