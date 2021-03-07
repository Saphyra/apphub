package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class HomePlanetSelector {
    private final AllianceMemberSystemFinder allianceMemberSystemFinder;
    private final RandomEmptyPlanetFinder randomEmptyPlanetFinder;

    public Planet selectPlanet(UUID userId, Collection<Alliance> alliances, Universe universe) {
        Optional<Alliance> allianceOptional = findAlliance(userId, alliances);
        if (allianceOptional.isPresent()) {
            List<UUID> members = allianceOptional.get()
                .getMembers()
                .values()
                .stream()
                .map(Player::getUserId)
                .collect(Collectors.toList());

            return allianceMemberSystemFinder.findAllianceMemberSystem(members, universe);
        } else {
            return randomEmptyPlanetFinder.randomEmptyPlanet(universe);
        }
    }

    private Optional<Alliance> findAlliance(UUID userId, Collection<Alliance> alliances) {
        return alliances.stream()
            .filter(alliance -> alliance.getMembers().containsKey(userId))
            .findFirst();
    }
}
