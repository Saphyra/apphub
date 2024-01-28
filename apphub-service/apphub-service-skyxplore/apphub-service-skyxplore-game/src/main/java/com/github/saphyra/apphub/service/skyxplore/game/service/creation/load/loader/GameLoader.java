package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.ChatFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoopFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickSchedulerLauncher;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameLoader {
    private final GameDao gameDao;
    private final PlayerLoader playerLoader;
    private final AllianceLoader allianceLoader;
    private final ChatFactory chatFactory;
    private final SkyXploreLobbyApiClient lobbyClient;
    private final EventLoopFactory eventLoopFactory;
    private final GameDataLoader gameDataLoader;
    private final ProcessLoader processLoader;
    private final TickSchedulerLauncher tickSchedulerLauncher;
    private final CommonConfigProperties commonConfigProperties;

    public void loadGame(GameModel gameModel, List<UUID> members) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        Map<UUID, Player> players = playerLoader.load(gameModel.getId(), members);

        Game game = Game.builder()
            .gameId(gameModel.getId())
            .gameName(gameModel.getName())
            .host(gameModel.getHost())
            .lastPlayed(gameModel.getLastPlayed())
            .players(players)
            .alliances(allianceLoader.load(gameModel.getId(), players))
            .data(gameDataLoader.load(gameModel.getId(), gameModel.getUniverseSize()))
            .chat(chatFactory.create(players.values()))
            .eventLoop(eventLoopFactory.create())
            .markedForDeletion(gameModel.getMarkedForDeletion())
            .markedForDeletionAt(gameModel.getMarkedForDeletionAt())
            .build();

        processLoader.loadProcesses(game);

        gameDao.save(game);
        tickSchedulerLauncher.launch(game);

        stopwatch.stop();
        log.info("Game loaded in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        lobbyClient.gameLoaded(game.getGameId(), commonConfigProperties.getDefaultLocale());
    }
}
