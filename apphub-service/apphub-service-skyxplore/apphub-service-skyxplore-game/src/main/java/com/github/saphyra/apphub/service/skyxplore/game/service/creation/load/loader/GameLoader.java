package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.ChatFactory;
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
    private final DateTimeUtil dateTimeUtil;
    private final GameDao gameDao;
    private final PlayerLoader playerLoader;
    private final AllianceLoader allianceLoader;
    private final ChatFactory chatFactory;
    private final UniverseLoader universeLoader;
    private final GameDataProxy gameDataProxy;
    private final MessageSenderProxy messageSenderProxy;

    public void loadGame(GameModel gameModel, List<UUID> members) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        gameModel.setLastPlayed(dateTimeUtil.getCurrentDate());
        Map<UUID, Player> players = playerLoader.load(gameModel.getId(), members);
        Game game = Game.builder()
            .gameId(gameModel.getId())
            .gameName(gameModel.getName())
            .host(gameModel.getHost())
            .lastPlayed(gameModel.getLastPlayed())
            .players(players)
            .alliances(allianceLoader.load(gameModel.getId(), players))
            .universe(universeLoader.load(gameModel.getId()))
            .chat(chatFactory.create(players.values()))
            .build();

        gameDataProxy.saveItem(gameModel);
        gameDao.save(game);

        stopwatch.stop();
        log.info("Game loaded in {}s", stopwatch.elapsed(TimeUnit.SECONDS));

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(members)
            .event(WebSocketEvent.builder().eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED).build())
            .build();
        messageSenderProxy.sendToLobby(message);
    }
}
