package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetToLocationResponseConverterTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String DEFAULT_NAME = "default-name";
    private static final Coordinate COORDINATE = new Coordinate(3124, 142);
    private static final UUID OWNER = UUID.randomUUID();
    private static final String OWNER_NAME = "owner-name";

    @InjectMocks
    private PlanetToLocationResponseConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Player player;

    @Test
    public void mapPlanets() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getDefaultName()).willReturn(DEFAULT_NAME);
        given(planet.getCoordinate()).willReturn(COORDINATE);
        given(planet.getOwner()).willReturn(OWNER);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(OWNER, player));
        given(player.getUsername()).willReturn(OWNER_NAME);

        List<PlanetLocationResponse> result = underTest.mapPlanets(Arrays.asList(planet), game);

        assertThat(result.get(0).getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.get(0).getPlanetName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.get(0).getCoordinate()).isEqualTo(COORDINATE);
        assertThat(result.get(0).getOwner()).isEqualTo(OWNER);
        assertThat(result.get(0).getOwnerName()).isEqualTo(OWNER_NAME);
    }
}