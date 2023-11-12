package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreGameProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoadGameServiceTest {
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @Mock
    private SkyXploreGameProxy gameProxy;

    @InjectMocks
    private LoadGameService underTest;

    @Mock
    private Lobby lobby;

    @Test
    public void loadGame() {
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getGameId()).willReturn(GAME_ID);
        Map<UUID, LobbyPlayer> players = CollectionUtils.singleValueMap(HOST, null);
        given(lobby.getPlayers()).willReturn(players);

        underTest.loadGame(lobby);

        ArgumentCaptor<SkyXploreLoadGameRequest> gameCreationArgumentCaptor = ArgumentCaptor.forClass(SkyXploreLoadGameRequest.class);
        verify(gameProxy).loadGame(gameCreationArgumentCaptor.capture());
        SkyXploreLoadGameRequest loadGameRequest = gameCreationArgumentCaptor.getValue();
        assertThat(loadGameRequest.getHost()).isEqualTo(HOST);
        assertThat(loadGameRequest.getGameId()).isEqualTo(GAME_ID);
        assertThat(loadGameRequest.getPlayers()).containsExactly(HOST);

        verify(lobby).setGameCreationStarted(true);

        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEvent.builder().eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED).build());
    }
}