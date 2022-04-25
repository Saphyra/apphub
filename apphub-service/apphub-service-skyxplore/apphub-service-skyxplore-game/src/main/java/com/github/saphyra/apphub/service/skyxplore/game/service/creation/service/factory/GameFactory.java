package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.BackgroundProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoopFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.HomePlanetSetupService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player.AiFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player.PlayerFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.universe.UniverseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameFactory {
    private final AllianceFactory allianceFactory;
    private final IdGenerator idGenerator;
    private final UniverseFactory universeFactory;
    private final ChatFactory chatFactory;
    private final HomePlanetSetupService homePlanetSetupService;
    private final DateTimeUtil dateTimeUtil;
    private final PlayerFactory playerFactory;
    private final AiFactory aiFactory;
    private final EventLoopFactory eventLoopFactory;
    private final BackgroundProcessFactory backgroundProcessFactory;

    public Game create(SkyXploreGameCreationRequest request) {
        UUID gameId = idGenerator.randomUuid();

        Map<UUID, Player> players = playerFactory.create(request.getMembers());
        List<Player> ais = aiFactory.generateAis(request, players.values());
        Map<UUID, Alliance> alliances = allianceFactory.create(request.getAlliances(), request.getMembers(), players);

        ais.forEach(player -> players.put(player.getUserId(), player));

        Universe universe = universeFactory.create(gameId, players.size(), request.getSettings());

        log.info("Setting up home planets for {} number of players...", players.size());
        players.values()
            .forEach(player -> homePlanetSetupService.setUpHomePlanet(player, alliances.values(), universe.getSystems()));
        log.info("Home planets are set up.");

        log.info("Game generated.");
        Game result = Game.builder()
            .gameId(gameId)
            .host(request.getHost())
            .players(players)
            .alliances(alliances)
            .universe(universe)
            .chat(chatFactory.create(request.getMembers()))
            .gameName(request.getGameName())
            .lastPlayed(dateTimeUtil.getCurrentTime())
            .eventLoop(eventLoopFactory.create())
            .markedForDeletion(false)
            .build();

        result.setBackgroundProcesses(backgroundProcessFactory.create(result));

        return result;
    }
}
