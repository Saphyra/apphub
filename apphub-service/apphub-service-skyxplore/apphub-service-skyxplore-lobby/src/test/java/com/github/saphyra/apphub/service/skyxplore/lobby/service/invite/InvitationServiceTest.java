package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InvitationServiceTest {
    private static final int FLOODING_LIMIT_SECONDS = 3124;
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final String PLAYER_NAME = "player-name";

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private InvitationFactory invitationFactory;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private SkyXploreDataProxy dataProxy;

    private InvitationService underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Mock
    private Lobby lobby;

    @Mock
    private Invitation invitation;

    @Mock
    private Invitation newInvitation;

    @BeforeEach
    public void setUp() {
        underTest = InvitationService.builder()
            .lobbyDao(lobbyDao)
            .invitationFactory(invitationFactory)
            .dateTimeUtil(dateTimeUtil)
            .characterProxy(characterProxy)
            .messageSenderProxy(messageSenderProxy)
            .dataProxy(dataProxy)
            .floodingLimitSeconds(FLOODING_LIMIT_SECONDS)
            .build();

        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void notFriends() {
        given(dataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(friendshipResponse));
        given(friendshipResponse.getFriendId()).willReturn(UUID.randomUUID());


        Throwable ex = catchThrowable(() -> underTest.invite(accessTokenHeader, FRIEND_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.PRECONDITION_FAILED, ErrorCode.GENERAL_ERROR);
    }

    @Test
    public void flooding() {
        given(dataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(friendshipResponse));
        given(friendshipResponse.getFriendId()).willReturn(FRIEND_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(Arrays.asList(invitation));
        given(invitation.getCharacterId()).willReturn(FRIEND_ID);
        given(invitation.getInvitorId()).willReturn(USER_ID);
        given(invitation.getInvitationTime()).willReturn(CURRENT_DATE);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        Throwable ex = catchThrowable(() -> underTest.invite(accessTokenHeader, FRIEND_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.TOO_MANY_REQUESTS, ErrorCode.TOO_FREQUENT_INVITATIONS);
    }

    @Test
    public void sendInvitation_invitedByDifferentPlayer() {
        given(dataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(friendshipResponse));
        given(friendshipResponse.getFriendId()).willReturn(FRIEND_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(invitation));
        given(invitation.getCharacterId()).willReturn(FRIEND_ID);
        given(invitation.getInvitorId()).willReturn(UUID.randomUUID());
        given(invitationFactory.create(USER_ID, FRIEND_ID)).willReturn(newInvitation);
        given(characterProxy.getCharacter()).willReturn(SkyXploreCharacterModel.builder().name(PLAYER_NAME).build());

        underTest.invite(accessTokenHeader, FRIEND_ID);

        assertThat(lobby.getInvitations()).containsExactlyInAnyOrder(invitation, newInvitation);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToMainMenu(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(FRIEND_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION);

        InvitationMessage payload = (InvitationMessage) event.getPayload();
        assertThat(payload.getSenderId()).isEqualTo(USER_ID);
        assertThat(payload.getSenderName()).isEqualTo(PLAYER_NAME);
    }

    @Test
    public void sendInvitation_lastInvitationNotTooRecent() {
        given(dataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(friendshipResponse));
        given(friendshipResponse.getFriendId()).willReturn(FRIEND_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(invitation));
        given(invitation.getCharacterId()).willReturn(FRIEND_ID);
        given(invitation.getInvitorId()).willReturn(USER_ID);
        given(invitation.getInvitationTime()).willReturn(CURRENT_DATE.minusSeconds(FLOODING_LIMIT_SECONDS));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);
        given(invitationFactory.create(USER_ID, FRIEND_ID)).willReturn(newInvitation);
        given(characterProxy.getCharacter()).willReturn(SkyXploreCharacterModel.builder().name(PLAYER_NAME).build());

        underTest.invite(accessTokenHeader, FRIEND_ID);

        assertThat(lobby.getInvitations()).containsExactlyInAnyOrder(invitation, newInvitation);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToMainMenu(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(FRIEND_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION);

        InvitationMessage payload = (InvitationMessage) event.getPayload();
        assertThat(payload.getSenderId()).isEqualTo(USER_ID);
        assertThat(payload.getSenderName()).isEqualTo(PLAYER_NAME);
    }

    @Test
    public void inviteDirectly_forbiddenOperation() {
        given(lobby.getType()).willReturn(LobbyType.LOAD_GAME);

        Throwable ex = catchThrowable(() -> underTest.inviteDirectly(USER_ID, FRIEND_ID, lobby));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }
}