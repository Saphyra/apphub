package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.home_planet;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class HomePlanetSelector {
    private final AllianceMemberSystemFinder allianceMemberSystemFinder;
    private final RandomEmptyPlanetFinder randomEmptyPlanetFinder;

    public Planet selectPlanet(UUID userId, Collection<Alliance> alliances, Map<Coordinate, SolarSystem> solarSystems) {
        Optional<Alliance> allianceOptional = findAlliance(userId, alliances);
        if (allianceOptional.isPresent()) {
            List<UUID> members = allianceOptional.get()
                .getMembers()
                .values()
                .stream()
                .map(Player::getUserId)
                .collect(Collectors.toList());

            return allianceMemberSystemFinder.findAllianceMemberSystem(members, solarSystems);
        } else {
            return randomEmptyPlanetFinder.randomEmptyPlanet(solarSystems.values());
        }
    }

    private Optional<Alliance> findAlliance(UUID userId, Collection<Alliance> alliances) {
        return alliances.stream()
            .filter(alliance -> alliance.getMembers().containsKey(userId))
            .findFirst();
    }
}
