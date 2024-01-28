package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.PauseGameService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PlayerConnectionCheckerTickTaskTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Integer PAUSE_GAME_AFTER_DISCONNECTED_SECONDS = 312;
    private static final UUID HOST = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";

    @Mock
    private PauseGameService pauseGameService;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private SkyXploreGameMainWebSocketHandler mainWebSocketHandler;

    @InjectMocks
    private PlayerConnectionCheckerTickTask underTest;

    @Mock
    private Game game;

    @Mock
    private Player player;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.PLAYER_CONNECTION_CHECKER);
    }

    @Test
    void playerConnected() {
        given(game.getPlayers()).willReturn(Map.of(UUID.randomUUID(), player));
        given(player.isConnected()).willReturn(true);

        underTest.process(game);

        then(pauseGameService).shouldHaveNoInteractions();
        then(mainWebSocketHandler).shouldHaveNoInteractions();
    }

    @Test
    void nullPlayerDisconnectedAt() {
        given(game.getPlayers()).willReturn(Map.of(UUID.randomUUID(), player));
        given(player.isConnected()).willReturn(false);
        given(player.getDisconnectedAt()).willReturn(null);

        underTest.process(game);

        then(pauseGameService).shouldHaveNoInteractions();
        then(mainWebSocketHandler).shouldHaveNoInteractions();
    }

    @Test
    void disconnectedTimeoutNotElapsed() {
        given(game.getPlayers()).willReturn(Map.of(UUID.randomUUID(), player));
        given(player.isConnected()).willReturn(false);
        given(player.getDisconnectedAt()).willReturn(CURRENT_TIME.minusSeconds(PAUSE_GAME_AFTER_DISCONNECTED_SECONDS - 1));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(gameProperties.getPauseGameAfterDisconnectionSeconds()).willReturn(PAUSE_GAME_AFTER_DISCONNECTED_SECONDS);

        underTest.process(game);

        then(pauseGameService).shouldHaveNoInteractions();
        then(mainWebSocketHandler).shouldHaveNoInteractions();
    }

    @Test
    void pauseGame() {
        given(game.getPlayers()).willReturn(Map.of(UUID.randomUUID(), player));
        given(player.isConnected()).willReturn(false);
        given(player.getDisconnectedAt()).willReturn(CURRENT_TIME.minusSeconds(PAUSE_GAME_AFTER_DISCONNECTED_SECONDS + 1));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(gameProperties.getPauseGameAfterDisconnectionSeconds()).willReturn(PAUSE_GAME_AFTER_DISCONNECTED_SECONDS);
        given(game.getHost()).willReturn(HOST);
        given(player.getPlayerName()).willReturn(PLAYER_NAME);

        underTest.process(game);

        then(pauseGameService).should().setPausedStatus(true, game);
        then(mainWebSocketHandler).should().sendEvent(HOST, WebSocketEventName.SKYXPLORE_GAME_PLAYER_DISCONNECTED, new OneParamResponse<>(PLAYER_NAME));
    }
}