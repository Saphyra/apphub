package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.home_planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.building.BuildingPlacementService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.citizen.PlanetPopulationService;
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
