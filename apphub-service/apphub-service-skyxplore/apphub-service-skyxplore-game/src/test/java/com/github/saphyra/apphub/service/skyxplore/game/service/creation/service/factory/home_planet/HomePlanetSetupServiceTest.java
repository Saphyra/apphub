package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.building.BuildingPlacementService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen.PlanetPopulationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HomePlanetSetupServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private HomePlanetSelector homePlanetSelector;

    @Mock
    private BuildingPlacementService buildingPlacementService;

    @Mock
    private PlanetPopulationService planetPopulationService;

    @Mock
    private DefaultFoodProvider defaultFoodProvider;

    @InjectMocks
    private HomePlanetSetupService underTest;

    @Mock
    private Player player;

    @Mock
    private Alliance alliance;

    @Mock
    private Planet planet;

    @Test
    public void setUpHomePlanet() {
        given(player.getUserId()).willReturn(USER_ID);
        given(homePlanetSelector.selectPlanet(USER_ID, Arrays.asList(alliance), Collections.emptyMap())).willReturn(planet);

        underTest.setUpHomePlanet(player, Arrays.asList(alliance), Collections.emptyMap());

        verify(planet).setOwner(USER_ID);
        verify(buildingPlacementService).placeDefaultBuildings(planet);
        verify(planetPopulationService).addCitizens(planet);
        verify(defaultFoodProvider).setDefaultFoodSettings(planet);
    }
}