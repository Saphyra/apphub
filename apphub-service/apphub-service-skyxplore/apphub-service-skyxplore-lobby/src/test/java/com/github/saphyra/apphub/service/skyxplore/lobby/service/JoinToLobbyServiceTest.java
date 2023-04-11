package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JoinToLobbyServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID INVITOR_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";
    private static final UUID HOST = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private JoinToLobbyService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Invitation invitation;

    @Mock
    private Member member;

    @Mock
    private Alliance alliance;

    @Test
    public void acceptInvitation_notInvited() {
        given(lobbyDao.findByUserIdValidated(INVITOR_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(Arrays.asList(invitation));
        given(invitation.getCharacterId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.acceptInvitation(USER_ID, INVITOR_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    public void acceptInvitation() {
        given(lobbyDao.findByUserIdValidated(INVITOR_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(invitation));
        given(invitation.getCharacterId()).willReturn(USER_ID);
        given(lobby.getMembers()).willReturn(new HashMap<>());

        underTest.acceptInvitation(USER_ID, INVITOR_ID);

        assertThat(lobby.getInvitations()).isEmpty();
        assertThat(lobby.getMembers()).containsEntry(USER_ID, Member.builder().userId(USER_ID).status(LobbyMemberStatus.NOT_READY).build());
    }

    @Test
    public void userJoinedToLobby() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, member));
        given(characterProxy.getCharacter(USER_ID)).willReturn(SkyXploreCharacterModel.builder().name(PLAYER_NAME).build());
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.getAlliances()).willReturn(Arrays.asList(alliance));
        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);
        given(member.getStatus()).willReturn(LobbyMemberStatus.NOT_READY);

        underTest.userJoinedToLobby(USER_ID);

        verify(member).setConnected(true);
        verify(member).setStatus(LobbyMemberStatus.NOT_READY);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_JOIN_TO_LOBBY);

        JoinToLobbyService.JoinMessage payload = (JoinToLobbyService.JoinMessage) event.getPayload();
        assertThat(payload.getCharacterName()).isEqualTo(PLAYER_NAME);
        assertThat(payload.isHost()).isFalse();
        assertThat(payload.getAlliances()).containsExactly(ALLIANCE_NAME);
        assertThat(payload.getStatus()).isEqualTo(LobbyMemberStatus.NOT_READY);
    }
}