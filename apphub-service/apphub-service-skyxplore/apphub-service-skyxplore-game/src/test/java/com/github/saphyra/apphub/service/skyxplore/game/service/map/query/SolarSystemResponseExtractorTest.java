package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystems;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SolarSystemResponseExtractorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID VISIBLE_SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID HIDDEN_SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final String CUSTOM_NAME = "custom-name";

    @Mock
    private VisibilityFacade visibilityFacade;

    @InjectMocks
    private SolarSystemResponseExtractor underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SolarSystem visibleSolarSystem;

    @Mock
    private SolarSystem hiddenSolarSystem;

    @Mock
    private Coordinates coordinates;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Planets planets;

    @Mock
    private Planet planet;

    @Test
    void getSolarSystems() {
        SolarSystems solarSystems = new SolarSystems();
        solarSystems.addAll(List.of(visibleSolarSystem, hiddenSolarSystem));

        given(gameData.getSolarSystems()).willReturn(solarSystems);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(gameData.getPlanets()).willReturn(planets);

        given(visibleSolarSystem.getSolarSystemId()).willReturn(VISIBLE_SOLAR_SYSTEM_ID);
        given(hiddenSolarSystem.getSolarSystemId()).willReturn(HIDDEN_SOLAR_SYSTEM_ID);

        given(visibilityFacade.isVisible(gameData, USER_ID, VISIBLE_SOLAR_SYSTEM_ID)).willReturn(true);
        given(visibilityFacade.isVisible(gameData, USER_ID, HIDDEN_SOLAR_SYSTEM_ID)).willReturn(false);

        given(coordinates.findByReferenceIdValidated(VISIBLE_SOLAR_SYSTEM_ID)).willReturn(coordinate);
        given(visibleSolarSystem.getCustomNames()).willReturn(CollectionUtils.singleValueMap(USER_ID, CUSTOM_NAME, new OptionalHashMap<>()));
        given(planets.getBySolarSystemId(VISIBLE_SOLAR_SYSTEM_ID)).willReturn(List.of(planet));

        List<MapSolarSystemResponse> result = underTest.getSolarSystems(USER_ID, gameData);

        assertThat(result).hasSize(1);
        MapSolarSystemResponse response = result.get(0);
        assertThat(response.getSolarSystemId()).isEqualTo(VISIBLE_SOLAR_SYSTEM_ID);
        assertThat(response.getCoordinate()).isEqualTo(coordinate);
        assertThat(response.getSolarSystemName()).isEqualTo(CUSTOM_NAME);
        assertThat(response.getPlanetNum()).isEqualTo(1);
    }
}