package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.PreconditionFailedException;
import com.github.saphyra.apphub.lib.exception.TooManyRequestsException;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
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

        assertThat(ex).isInstanceOf(PreconditionFailedException.class);
    }

    @Test
    public void flooding() {
        given(dataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(friendshipResponse));
        given(friendshipResponse.getFriendId()).willReturn(FRIEND_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(Arrays.asList(invitation));
        given(invitation.getCharacterId()).willReturn(FRIEND_ID);
        given(invitation.getInvitationTime()).willReturn(CURRENT_DATE);
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        Throwable ex = catchThrowable(() -> underTest.invite(accessTokenHeader, FRIEND_ID));

        assertThat(ex).isInstanceOf(TooManyRequestsException.class);
        TooManyRequestsException exception = (TooManyRequestsException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.TOO_FREQUENT_INVITATIONS.name());
    }

    @Test
    public void sendInvitation() {
        given(dataProxy.getFriends(accessTokenHeader)).willReturn(Arrays.asList(friendshipResponse));
        given(friendshipResponse.getFriendId()).willReturn(FRIEND_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(invitation));
        given(invitation.getCharacterId()).willReturn(FRIEND_ID);
        given(invitation.getInvitationTime()).willReturn(CURRENT_DATE.minusSeconds(FLOODING_LIMIT_SECONDS));
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(invitationFactory.create(FRIEND_ID)).willReturn(newInvitation);
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
}