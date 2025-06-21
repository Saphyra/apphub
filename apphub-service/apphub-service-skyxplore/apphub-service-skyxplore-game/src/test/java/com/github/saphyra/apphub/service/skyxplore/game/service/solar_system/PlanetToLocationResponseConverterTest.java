package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate.Coordinates;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
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

@ExtendWith(MockitoExtension.class)
class PlanetToLocationResponseConverterTest {
    private static final UUID SOLAR_SYSTEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String PLANET_NAME = "planet-name";
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";

    @Mock
    private VisibilityFacade visibilityFacade;

    @InjectMocks
    private PlanetToLocationResponseConverter underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Planets planets;

    @Mock
    private Planet visiblePlanet;

    @Mock
    private Planet hiddenPlanet;

    @Mock
    private Coordinates coordinates;

    @Mock
    private Coordinate coordinate;

    @Mock
    private Player player;

    @Test
    void mapPlanets() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(planets);
        given(planets.getBySolarSystemId(SOLAR_SYSTEM_ID)).willReturn(List.of(visiblePlanet, hiddenPlanet));
        given(visibilityFacade.isVisible(USER_ID, visiblePlanet)).willReturn(true);
        given(visibilityFacade.isVisible(USER_ID, hiddenPlanet)).willReturn(false);
        given(gameData.getCoordinates()).willReturn(coordinates);
        given(visiblePlanet.getPlanetId()).willReturn(PLANET_ID);
        given(coordinates.findByReferenceIdValidated(PLANET_ID)).willReturn(coordinate);
        given(visiblePlanet.getCustomNames()).willReturn(CollectionUtils.singleValueMap(USER_ID, PLANET_NAME, new OptionalHashMap<>()));
        given(visiblePlanet.getOwner()).willReturn(OWNER_ID);
        given(game.getPlayers()).willReturn(Map.of(OWNER_ID, player));
        given(player.getPlayerName()).willReturn(PLAYER_NAME);

        List<PlanetLocationResponse> result = underTest.mapPlanets(game, SOLAR_SYSTEM_ID, USER_ID);

        assertThat(result).hasSize(1);
        PlanetLocationResponse response = result.get(0);
        assertThat(response.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(response.getPlanetName()).isEqualTo(PLANET_NAME);
        assertThat(response.getCoordinate()).isEqualTo(coordinate);
        assertThat(response.getOwner()).isEqualTo(OWNER_ID);
        assertThat(response.getOwnerName()).isEqualTo(PLAYER_NAME);
    }
}