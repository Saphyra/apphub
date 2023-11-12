package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExitFromLobbyService {
    private final LobbyDao lobbyDao;
    private final InvitationRejectionService invitationRejectionService;
    private final ExitMessageSender exitMessageSender;

    public void exit(UUID userId) {
        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exit(userId, lobby));
    }

    public void exit(UUID userId, Lobby lobby) {
        lobby.getPlayers()
            .remove(userId);

        invitationRejectionService.rejectInvitations(userId, lobby);
        exitMessageSender.sendExitMessage(userId, lobby, false);

        if (lobby.getHost().equals(userId)) {
            log.info("Host left the lobby. Deleting...");
            lobbyDao.delete(lobby);
        }
    }
}
