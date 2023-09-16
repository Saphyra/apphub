package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyInvitationWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InvitationRejectionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID INVITED_CHARACTER_ID = UUID.randomUUID();

    @Mock
    private SkyXploreLobbyInvitationWebSocketHandler invitationWebSocketHandler;

    @Mock
    private ExitMessageSender exitMessageSender;

    @InjectMocks
    private InvitationRejectionService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Invitation invitation;

    @Test
    void rejectInvitations() {
        given(lobby.getInvitations()).willReturn(CollectionUtils.toList(invitation));
        given(invitation.getInvitorId()).willReturn(USER_ID);
        given(invitation.getCharacterId()).willReturn(INVITED_CHARACTER_ID);

        underTest.rejectInvitations(USER_ID, lobby);

        then(invitationWebSocketHandler).should().sendEvent(INVITED_CHARACTER_ID, WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION, USER_ID);
        then(exitMessageSender).should().sendExitMessage(INVITED_CHARACTER_ID, lobby, true);
        assertThat(lobby.getInvitations()).isEmpty();
    }
}