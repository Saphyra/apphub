package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlanetLoaderTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final String CUSTOM_NAME = "custom-name";
    private static final Integer SIZE = 3426;
    private static final Double ORBIT_RADIUS = 264d;
    private static final Double ORBIT_SPEED = 346d;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private PlanetLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Mock
    private PlanetModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.PLANET);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(PlanetModel[].class);
    }

    @Test
    void addToGameData() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPlanets()).willReturn(planets);

        underTest.addToGameData(gameData, List.of(planet));

        verify(planets).put(PLANET_ID, planet);
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(PLANET_ID);
        given(model.getSolarSystemId()).willReturn(SOLAR_SYSTEM_ID);
        given(model.getDefaultName()).willReturn(DEFAULT_NAME);
        given(model.getCustomNames()).willReturn(Map.of(USER_ID, CUSTOM_NAME));
        given(model.getSize()).willReturn(SIZE);
        given(model.getOrbitRadius()).willReturn(ORBIT_RADIUS);
        given(model.getOrbitSpeed()).willReturn(ORBIT_SPEED);
        given(model.getOwner()).willReturn(OWNER_ID);

        Planet result = underTest.convert(model);

        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSolarSystemId()).isEqualTo(SOLAR_SYSTEM_ID);
        assertThat(result.getDefaultName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getCustomNames()).containsEntry(USER_ID, CUSTOM_NAME);
        assertThat(result.getSize()).isEqualTo(SIZE);
        assertThat(result.getOrbitRadius()).isEqualTo(ORBIT_RADIUS);
        assertThat(result.getOrbitSpeed()).isEqualTo(ORBIT_SPEED);
        assertThat(result.getOwner()).isEqualTo(OWNER_ID);
    }
}