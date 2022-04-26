package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
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

        Map<UUID, PlayerModel> players = fetchPlayers(gameId);

        return GameViewForLobbyCreation.builder()
            .hostAllianceId(players.get(host).getAllianceId())
            .name(gameModel.getName())
            .alliances(allianceDao.getByGameId(gameId))
            .players(new ArrayList<>(players.values()))
            .build();
    }

    private Map<UUID, PlayerModel> fetchPlayers(UUID gameId) {
        return playerDao.getByGameId(gameId)
            .stream()
            .filter(playerModel -> !playerModel.getAi())
            .collect(Collectors.toMap(PlayerModel::getUserId, Function.identity()));
    }
}
