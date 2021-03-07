package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.home_planet.HomePlanetSetupService;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.player.PlayerPopulationService;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.universe.UniverseFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GameFactory {
    private final AllianceFactory allianceFactory;
    private final IdGenerator idGenerator;
    private final PlayerPopulationService playerPopulationService;
    private final UniverseFactory universeFactory;
    private final ChatFactory chatFactory;
    private final HomePlanetSetupService homePlanetSetupService;

    public Game create(SkyXploreGameCreationRequest request) {
        Universe universe = universeFactory.create(request.getMembers().size(), request.getSettings());
        Map<UUID, Player> players = playerPopulationService.populateGameWithPlayers(request.getMembers().keySet(), getPlanetCount(universe), request.getSettings());
        Map<UUID, Alliance> alliances = allianceFactory.create(request.getAlliances(), request.getMembers(), players);

        log.info("Setting up home planets...");
        players.values()
            .forEach(player -> homePlanetSetupService.setUpHomePlanet(player, alliances.values(), universe));
        log.info("Home planets are set up.");

        log.info("Game generated.");
        return Game.builder()
            .gameId(idGenerator.randomUuid())
            .host(request.getHost())
            .players(players)
            .alliances(alliances)
            .universe(universe)
            .chat(chatFactory.create(request.getMembers()))
            .gameName(request.getGameName())
            .build();
    }

    private int getPlanetCount(Universe universe) {
        return (int) universe.getSystems()
            .values()
            .stream()
            .mapToLong(solarSystem -> solarSystem.getPlanets().values().size())
            .sum();
    }
}
