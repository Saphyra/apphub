package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.building;

import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.citizen.CitizenFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class HomePlanetSetupService {
    private final HomePlanetSelector homePlanetSelector;
    private final BuildingPlacementService buildingPlacementService;
    private final CitizenFactory citizenFactory;

    public void setUpHomePlanet(Player player, Collection<Alliance> alliances, Universe universe) {
        Planet planet = homePlanetSelector.selectPlanet(player.getUserId(), alliances, universe);
        planet.setOwner(player.getUserId());
        buildingPlacementService.placeDefaultBuildings(planet);
        citizenFactory.addCitizens(planet);
    }
}
