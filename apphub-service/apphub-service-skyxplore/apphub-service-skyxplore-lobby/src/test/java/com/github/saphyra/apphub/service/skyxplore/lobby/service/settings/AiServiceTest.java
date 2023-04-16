package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.GameSettingsProperties;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID AI_USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private GameSettingsProperties gameSettingsProperties;

    @InjectMocks
    private AiService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private AiPlayer aiPlayer;

    @Test
    void removeAi() {
        given(lobbyDao.findByHostValidated(USER_ID)).willReturn(lobby);
        List<AiPlayer> ais = CollectionUtils.toList(aiPlayer);
        given(lobby.getAis()).willReturn(ais);
        given(aiPlayer.getUserId()).willReturn(AI_USER_ID);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, null));

        underTest.removeAi(USER_ID, AI_USER_ID);

        assertThat(ais).isEmpty();

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_AI_REMOVED);
        assertThat(message.getEvent().getPayload()).isEqualTo(AI_USER_ID);
    }

    @Test
    void createAi() {
        given(lobbyDao.findByHostValidated(USER_ID)).willReturn(lobby);
        List<AiPlayer> aiPlayers = new ArrayList<>();
        given(lobby.getAis()).willReturn(aiPlayers);
        given(idGenerator.randomUuid()).willReturn(AI_USER_ID);
        given(gameSettingsProperties.getMaxAiCount()).willReturn(1);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, null));

        AiPlayer aiPlayer = AiPlayer.builder()
            .build();

        underTest.createOrModifyAi(USER_ID, aiPlayer);

        assertThat(aiPlayer.getUserId()).isEqualTo(AI_USER_ID);
        assertThat(aiPlayers).containsExactly(aiPlayer);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED);
        assertThat(message.getEvent().getPayload()).isEqualTo(aiPlayer);
    }

    @Test
    void modifyAi() {
        AiPlayer aiPlayer = AiPlayer.builder()
            .userId(AI_USER_ID)
            .build();

        given(lobbyDao.findByHostValidated(USER_ID)).willReturn(lobby);
        List<AiPlayer> aiPlayers = new ArrayList<>();
        aiPlayers.add(this.aiPlayer);
        given(this.aiPlayer.getUserId()).willReturn(AI_USER_ID);
        given(lobby.getAis()).willReturn(aiPlayers);
        given(gameSettingsProperties.getMaxAiCount()).willReturn(1);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, null));

        underTest.createOrModifyAi(USER_ID, aiPlayer);

        assertThat(aiPlayer.getUserId()).isEqualTo(AI_USER_ID);
        assertThat(aiPlayers).containsExactly(aiPlayer);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED);
        assertThat(message.getEvent().getPayload()).isEqualTo(aiPlayer);
    }

    @Test
    void aiLimitReached() {
        AiPlayer aiPlayer = AiPlayer.builder()
            .build();

        given(lobbyDao.findByHostValidated(USER_ID)).willReturn(lobby);
        List<AiPlayer> aiPlayers = new ArrayList<>();
        aiPlayers.add(this.aiPlayer);
        given(lobby.getAis()).willReturn(aiPlayers);
        given(gameSettingsProperties.getMaxAiCount()).willReturn(1);

        Throwable ex = catchThrowable(() -> underTest.createOrModifyAi(USER_ID, aiPlayer));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.BAD_REQUEST, ErrorCode.TOO_MANY_AIS);
    }
}