package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LobbyCreationService {
    private final LobbyNameValidator lobbyNameValidator;
    private final ExitFromLobbyService exitFromLobbyService;
    private final LobbyDao lobbyDao;
    private final LobbyFactory lobbyFactory;
    private final SkyXploreDataProxy skyXploreDataProxy;
    private final InvitationService invitationService;

    public void createNew(UUID userId, String lobbyName) {
        lobbyNameValidator.validate(lobbyName);

        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exitFromLobbyService.exit(userId, lobby));

        Lobby lobby = lobbyFactory.createForNewGame(userId, lobbyName);
        lobbyDao.save(lobby);
    }

    public void createForExistingGame(UUID userId, UUID gameId) {
        GameViewForLobbyCreation game = skyXploreDataProxy.getGameForLobbyCreation(gameId);

        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exitFromLobbyService.exit(userId, lobby));

        Lobby lobby = lobbyFactory.createForLoadGame(
            userId,
            gameId,
            game
        );

        lobbyDao.save(lobby);

        game.getPlayers()
            .stream()
            .filter(playerModel -> !playerModel.getUserId().equals(userId))
            .forEach(playerModel -> invitationService.inviteDirectly(userId, playerModel.getUserId(), lobby));
    }
}
