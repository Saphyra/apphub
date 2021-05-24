package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameToGameItemListConverter {
    private final AllianceToModelConverter allianceConverter;
    private final PlayerToModelConverter playerConverter;
    private final UniverseToModelConverter universeConverter;

    public List<GameItem> convertDeep(Game game) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(game));
        result.addAll(convertPlayers(game));
        result.addAll(convertAlliances(game));
        result.addAll(convertUniverse(game));
        return result;
    }

    private List<GameItem> convertUniverse(Game game) {
        return universeConverter.convertDeep(game.getUniverse(), game);
    }

    private List<GameItem> convertAlliances(Game game) {
        return game.getAlliances()
            .values()
            .stream()
            .map(alliance -> allianceConverter.convert(alliance, game))
            .collect(Collectors.toList());
    }

    private List<GameItem> convertPlayers(Game game) {
        return game.getPlayers()
            .values()
            .stream()
            .map(player -> playerConverter.convert(player, game))
            .collect(Collectors.toList());
    }

    public GameItem convert(Game game) {
        GameModel gameModel = new GameModel();
        gameModel.setGameId(game.getGameId());
        gameModel.setId(game.getGameId());
        gameModel.setType(GameItemType.GAME);
        gameModel.setHost(game.getHost());
        gameModel.setName(game.getGameName());
        return gameModel;
    }
}
