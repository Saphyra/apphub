package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyInvitationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class InvitationRejectionService {
    private final SkyXploreLobbyInvitationWebSocketHandler invitationWebSocketHandler;
    private final ExitMessageSender exitMessageSender;

    void rejectInvitations(UUID userId, Lobby lobby) {
        lobby.getInvitations()
            .stream()
            .filter(invitation -> invitation.getInvitorId().equals(userId))
            .forEach(invitation -> rejectInvitation(invitation, lobby));

        lobby.getInvitations().removeIf(invitation -> invitation.getInvitorId().equals(userId));
    }

    private void rejectInvitation(Invitation invitation, Lobby lobby) {
        invitationWebSocketHandler.sendEvent(invitation.getCharacterId(), WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION, invitation.getInvitorId());
        //To remove the invited player from the player list
        exitMessageSender.sendExitMessage(invitation.getCharacterId(), lobby, true);
    }
}
