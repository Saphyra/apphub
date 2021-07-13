package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameLoader {
    private final DateTimeUtil dateTimeUtil;
    private final GameDao gameDao;
    private final PlayerLoader playerLoader;

    public void loadGame(GameModel gameModel, List<UUID> members) {
        Game game = Game.builder()
            .gameId(gameModel.getId())
            .gameName(gameModel.getName())
            .host(gameModel.getHost())
            .lastPlayed(dateTimeUtil.getCurrentDate())
            .players(playerLoader.load(gameModel.getId(), members))
            .alliances(null)//TODO
            .universe(null)//TODO
            .chat(null)//TODO
            .build();

        gameDao.save(game);
    }
}
