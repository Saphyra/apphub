package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ExitFromLobbyServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private ExitFromLobbyService underTest;

    @Mock
    private Lobby lobby;

    @Test
    public void memberLeft() {
        given(lobbyDao.findByUserId(MEMBER_ID)).willReturn(Optional.of(lobby));
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(
            new BiWrapper<>(USER_ID, null),
            new BiWrapper<>(MEMBER_ID, null)
        ));
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(Invitation.builder().invitorId(MEMBER_ID).characterId(USER_ID).build()));
        given(lobby.getExpectedPlayers()).willReturn(Arrays.asList(MEMBER_ID));

        given(characterProxy.getCharacter(MEMBER_ID)).willReturn(SkyXploreCharacterModel.builder().name(PLAYER_NAME).build());

        underTest.exit(MEMBER_ID);

        assertThat(lobby.getMembers()).containsOnlyKeys(USER_ID);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_EXIT_FROM_LOBBY);

        ExitFromLobbyService.ExitMessage payload = (ExitFromLobbyService.ExitMessage) event.getPayload();
        assertThat(payload.getUserId()).isEqualTo(MEMBER_ID);
        assertThat(payload.getCharacterName()).isEqualTo(PLAYER_NAME);
        assertThat(payload.isHost()).isFalse();
        assertThat(payload.isExpectedPlayer()).isTrue();

        verify(lobbyDao, times(0)).delete(any());

        ArgumentCaptor<WebSocketMessage> invitationArgumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToMainMenu(invitationArgumentCaptor.capture());
        WebSocketMessage invitationMessage = invitationArgumentCaptor.getValue();

        assertThat(invitationMessage.getRecipients()).containsExactly(USER_ID);
        assertThat(invitationMessage.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION);
        assertThat(invitationMessage.getEvent().getPayload()).isEqualTo(MEMBER_ID);
        assertThat(lobby.getInvitations()).isEmpty();
    }

    @Test
    public void hostLeft() {
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(lobby));
        given(lobby.getMembers()).willReturn(CollectionUtils.toMap(
            new BiWrapper<>(USER_ID, null),
            new BiWrapper<>(MEMBER_ID, null)
        ));
        given(lobby.getHost()).willReturn(USER_ID);
        Invitation remainingInvitation = Invitation.builder()
            .invitorId(UUID.randomUUID())
            .build();
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(Invitation.builder().invitorId(USER_ID).characterId(MEMBER_ID).build(), remainingInvitation));

        given(characterProxy.getCharacter(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(PLAYER_NAME).build());

        underTest.exit(USER_ID);

        assertThat(lobby.getMembers()).containsOnlyKeys(MEMBER_ID);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(MEMBER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_EXIT_FROM_LOBBY);

        ExitFromLobbyService.ExitMessage payload = (ExitFromLobbyService.ExitMessage) event.getPayload();
        assertThat(payload.getUserId()).isEqualTo(USER_ID);
        assertThat(payload.getCharacterName()).isEqualTo(PLAYER_NAME);
        assertThat(payload.isHost()).isTrue();

        verify(lobbyDao).delete(lobby);

        ArgumentCaptor<WebSocketMessage> invitationArgumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToMainMenu(invitationArgumentCaptor.capture());
        WebSocketMessage invitationMessage = invitationArgumentCaptor.getValue();

        assertThat(invitationMessage.getRecipients()).containsExactly(MEMBER_ID);
        assertThat(invitationMessage.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION);
        assertThat(invitationMessage.getEvent().getPayload()).isEqualTo(USER_ID);
        assertThat(lobby.getInvitations()).containsExactly(remainingInvitation);
    }
}