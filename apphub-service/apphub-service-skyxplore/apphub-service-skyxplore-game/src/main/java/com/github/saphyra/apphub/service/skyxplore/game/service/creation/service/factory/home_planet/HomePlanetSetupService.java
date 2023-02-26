package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.building.BuildingPlacementService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen.PlanetPopulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class HomePlanetSetupService {
    private final HomePlanetSelector homePlanetSelector;
    private final BuildingPlacementService buildingPlacementService;
    private final PlanetPopulationService planetPopulationService;
    private final DefaultFoodProvider defaultFoodProvider;

    public void setUpHomePlanet(Player player, Collection<Alliance> alliances, Map<Coordinate, SolarSystem> solarSystems) {
        Planet planet = homePlanetSelector.selectPlanet(player.getUserId(), alliances, solarSystems);
        planet.setOwner(player.getUserId());
        buildingPlacementService.placeDefaultBuildings(planet);
        planetPopulationService.addCitizens(planet);
        defaultFoodProvider.setDefaultFoodSettings(planet);
    }
}
