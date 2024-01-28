package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.PauseGameService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerConnectionCheckerTickTask implements TickTask {
    private final PauseGameService pauseGameService;
    private final GameProperties gameProperties;
    private final DateTimeUtil dateTimeUtil;
    private final SkyXploreGameMainWebSocketHandler mainWebSocketHandler;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.PLAYER_CONNECTION_CHECKER;
    }

    @Override
    public void process(Game game) {
        Optional<Player> maybeDisconnectedPlayer = getDisconnectedPlayer(game.getPlayers().values());
        if (maybeDisconnectedPlayer.isPresent()) {
            Player disconnectedPlayer = maybeDisconnectedPlayer.get();
            log.info("Player {} is disconnected. Pausing game...", disconnectedPlayer.getUserId());

            pauseGameService.setPausedStatus(true, game);

            mainWebSocketHandler.sendEvent(game.getHost(), WebSocketEventName.SKYXPLORE_GAME_PLAYER_DISCONNECTED, new OneParamResponse<>(disconnectedPlayer.getPlayerName()));
        }
    }

    private Optional<Player> getDisconnectedPlayer(Collection<Player> players) {
        return players.stream()
            .filter(player -> !player.isConnected())
            .filter(player -> !isNull(player.getDisconnectedAt()))
            .filter(player -> player.getDisconnectedAt().plusSeconds(gameProperties.getPauseGameAfterDisconnectionSeconds()).isBefore(dateTimeUtil.getCurrentDateTime()))
            .findAny();
    }
}
