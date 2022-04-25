package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SavedGameQueryService {
    private final GameDao gameDao;
    private final PlayerDao playerDao;
    private final DateTimeUtil dateTimeUtil;

    List<SavedGameResponse> getSavedGames(UUID userId) {
        return gameDao.getByHost(userId)
            .stream()
            .filter(gameModel -> !gameModel.getMarkedForDeletion())
            .map(gameModel -> SavedGameResponse.builder()
                .gameId(gameModel.getGameId())
                .gameName(gameModel.getName())
                .players(getPlayers(gameModel))
                .lastPlayed(dateTimeUtil.toEpochSecond(gameModel.getLastPlayed()))
                .build())
            .collect(Collectors.toList());
    }

    private String getPlayers(GameModel gameModel) {
        return playerDao.getByGameId(gameModel.getGameId())
            .stream()
            .filter(playerModel -> !playerModel.getAi())
            .filter(playerModel -> !gameModel.getHost().equals(playerModel.getUserId()))
            .map(PlayerModel::getUsername)
            .sorted(String::compareTo)
            .collect(Collectors.joining(", "));
    }
}
