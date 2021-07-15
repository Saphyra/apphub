package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        Lobby lobby = lobbyFactory.create(userId, lobbyName, LobbyType.NEW_GAME);
        lobbyDao.save(lobby);
    }

    public void createForExistingGame(UUID userId, UUID gameId) {
        GameViewForLobbyCreation game = skyXploreDataProxy.getGameForLobbyCreation(gameId);

        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exitFromLobbyService.exit(userId, lobby));

        Lobby lobby = lobbyFactory.create(
            userId,
            gameId,
            game.getHostAllianceId(),
            game.getName(),
            LobbyType.LOAD_GAME,
            mapAlliances(game.getAlliances()),
            game.getPlayers().stream().map(PlayerModel::getUserId).collect(Collectors.toList())
        );

        lobbyDao.save(lobby);

        game.getPlayers()
            .stream()
            .filter(playerModel -> !playerModel.getUserId().equals(userId))
            .forEach(playerModel -> invitationService.inviteDirectly(userId, playerModel.getUserId(), lobby));
    }

    private List<Alliance> mapAlliances(List<AllianceModel> alliances) {
        return alliances.stream()
            .map(allianceModel -> Alliance.builder().allianceId(allianceModel.getId()).allianceName(allianceModel.getName()).build())
            .collect(Collectors.toList());
    }
}
