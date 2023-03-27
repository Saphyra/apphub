package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreGameProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoadGameServiceTest {
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

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
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(HOST, null));

        underTest.loadGame(lobby);

        ArgumentCaptor<SkyXploreLoadGameRequest> gameCreationArgumentCaptor = ArgumentCaptor.forClass(SkyXploreLoadGameRequest.class);
        verify(gameProxy).loadGame(gameCreationArgumentCaptor.capture());
        SkyXploreLoadGameRequest loadGameRequest = gameCreationArgumentCaptor.getValue();
        assertThat(loadGameRequest.getHost()).isEqualTo(HOST);
        assertThat(loadGameRequest.getGameId()).isEqualTo(GAME_ID);
        assertThat(loadGameRequest.getMembers()).containsExactly(HOST);

        verify(lobby).setGameCreationStarted(true);

        verify(lobbyDao).delete(lobby);

        ArgumentCaptor<WebSocketMessage> messageArgumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(messageArgumentCaptor.capture());
        WebSocketMessage message = messageArgumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(HOST);
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED);
    }
}