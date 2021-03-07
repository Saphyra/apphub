package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building.BuildingPlacementService;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.citizen.PlanetPopulationService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HomePlanetSetupServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private HomePlanetSelector homePlanetSelector;

    @Mock
    private BuildingPlacementService buildingPlacementService;

    @Mock
    private PlanetPopulationService planetPopulationService;

    @InjectMocks
    private HomePlanetSetupService underTest;

    @Mock
    private Player player;

    @Mock
    private Alliance alliance;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Test
    public void setUpHomePlanet() {
        given(player.getUserId()).willReturn(USER_ID);
        given(homePlanetSelector.selectPlanet(USER_ID, Arrays.asList(alliance), universe)).willReturn(planet);

        underTest.setUpHomePlanet(player, Arrays.asList(alliance), universe);

        verify(planet).setOwner(USER_ID);
        verify(buildingPlacementService).placeDefaultBuildings(planet);
        verify(planetPopulationService).addCitizens(planet);
    }
}