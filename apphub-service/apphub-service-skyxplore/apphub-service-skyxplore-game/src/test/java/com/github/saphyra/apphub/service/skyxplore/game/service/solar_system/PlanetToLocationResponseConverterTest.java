package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.solar_system.PlanetLocationResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.visibility.VisibilityFacade;
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
    private static final UUID OWNER = UUID.randomUUID();
    private static final String OWNER_NAME = "owner-name";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private VisibilityFacade visibilityFacade;

    @InjectMocks
    private PlanetToLocationResponseConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Planet filteredPlanet;

    @Mock
    private Player player;

    @Mock
    private CoordinateModel coordinateModel;

    @Mock
    private Coordinate coordinate;

    @Test
    public void mapPlanets() {
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(planet.getDefaultName()).willReturn(DEFAULT_NAME);
        given(planet.getCustomNames()).willReturn(new OptionalHashMap<>());
        given(planet.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);
        given(planet.getOwner()).willReturn(OWNER);
        given(game.getPlayers()).willReturn(CollectionUtils.singleValueMap(OWNER, player));
        given(player.getPlayerName()).willReturn(OWNER_NAME);
        given(visibilityFacade.isVisible(USER_ID, planet)).willReturn(true);
        given(visibilityFacade.isVisible(USER_ID, filteredPlanet)).willReturn(false);

        List<PlanetLocationResponse> result = underTest.mapPlanets(USER_ID, Arrays.asList(planet, filteredPlanet), game);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.get(0).getPlanetName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.get(0).getCoordinate()).isEqualTo(coordinate);
        assertThat(result.get(0).getOwner()).isEqualTo(OWNER);
        assertThat(result.get(0).getOwnerName()).isEqualTo(OWNER_NAME);
    }
}