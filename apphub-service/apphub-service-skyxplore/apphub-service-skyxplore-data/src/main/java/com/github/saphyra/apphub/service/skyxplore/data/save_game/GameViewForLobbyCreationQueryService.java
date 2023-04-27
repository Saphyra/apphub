package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance.AllianceDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameViewForLobbyCreationQueryService {
    private final GameDao gameDao;
    private final PlayerDao playerDao;
    private final AllianceDao allianceDao;

    public GameViewForLobbyCreation getView(UUID host, UUID gameId) {
        GameModel gameModel = gameDao.findByIdValidated(gameId);

        if (!gameModel.getHost().equals(host)) {
            throw ExceptionFactory.forbiddenOperation(host + " has no access to game " + gameId);
        }

        if (gameModel.getMarkedForDeletion()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.LOCKED, ErrorCode.GAME_DELETED, gameId + " is marked for deletion.");
        }

        List<PlayerModel> allPlayers = playerDao.getByGameId(gameId);
        Map<UUID, PlayerModel> players = fetchPlayers(allPlayers);

        return GameViewForLobbyCreation.builder()
            .hostAllianceId(players.get(host).getAllianceId())
            .name(gameModel.getName())
            .alliances(allianceDao.getByGameId(gameId))
            .players(new ArrayList<>(players.values()))
            .ais(fetchAis(allPlayers))
            .build();
    }

    private List<AiPlayer> fetchAis(List<PlayerModel> allPlayers) {
        return allPlayers.stream()
            .filter(PlayerModel::getAi)
            .map(playerModel -> AiPlayer.builder()
                .userId(playerModel.getUserId())
                .name(playerModel.getUsername())
                .allianceId(playerModel.getAllianceId())
                .build())
            .collect(Collectors.toList());
    }

    private Map<UUID, PlayerModel> fetchPlayers(List<PlayerModel> allPlayers) {
        return allPlayers
            .stream()
            .filter(playerModel -> !playerModel.getAi())
            .collect(Collectors.toMap(PlayerModel::getUserId, Function.identity()));
    }
}
