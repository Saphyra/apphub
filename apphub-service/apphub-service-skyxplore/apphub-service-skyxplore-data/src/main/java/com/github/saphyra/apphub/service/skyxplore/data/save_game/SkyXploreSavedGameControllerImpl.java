package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreSavedGameController;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.response.SavedGameResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.game.GameDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.player.PlayerDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
@Slf4j
public class SkyXploreSavedGameControllerImpl implements SkyXploreSavedGameController {
    private final OptionalMap<GameItemType, GameItemService> savers;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final GameDao gameDao;
    private final PlayerDao playerDao;
    private final GameDeletionService gameDeletionService;

    public SkyXploreSavedGameControllerImpl(List<GameItemService> savers, ObjectMapperWrapper objectMapperWrapper, GameDao gameDao, PlayerDao playerDao, GameDeletionService gameDeletionService) {
        this.savers = new OptionalHashMap<>(savers.stream()
            .collect(Collectors.toMap(GameItemService::getType, Function.identity())));
        this.objectMapperWrapper = objectMapperWrapper;
        this.gameDao = gameDao;
        this.playerDao = playerDao;
        this.gameDeletionService = gameDeletionService;
    }

    @Override
    public void saveGameData(List<Object> items) {
        log.info("Saving {} number of gameItems for game {}...", items.size(), objectMapperWrapper.convertValue(items.get(0), GameItem.class).getGameId());
        items.stream()
            .map(o -> new BiWrapper<>(o, objectMapperWrapper.convertValue(o, GameItem.class).getType()))
            .filter(this::isTypeFilled)
            .collect(Collectors.groupingBy(BiWrapper::getEntity2))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(BiWrapper::getEntity1).collect(Collectors.toList())))
            .forEach(this::save);
    }

    @Override
    public List<SavedGameResponse> getSavedGames(AccessTokenHeader accessTokenHeader) {
        log.info("Querying saved games for {}", accessTokenHeader.getUserId());
        return gameDao.getByHost(accessTokenHeader.getUserId())
            .stream()
            .map(gameModel -> SavedGameResponse.builder()
                .gameId(gameModel.getGameId())
                .gameName(gameModel.getName())
                .players(getPlayers(gameModel))
                .lastPlayed(gameModel.getLastPlayed())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public void deleteGame(UUID gameId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete game {}", accessTokenHeader.getUserId(), gameId);
        gameDeletionService.deleteByGameId(gameId, accessTokenHeader.getUserId());
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

    private boolean isTypeFilled(BiWrapper<Object, GameItemType> biWrapper) {
        boolean result = isNull(biWrapper.getEntity2());
        if (result) {
            log.warn("Null type for gameItem {}", biWrapper.getEntity1());
        }
        return !result;
    }

    private void save(GameItemType gameItemType, List<Object> gameItemObjects) {
        try {
            log.info("Saving {} number of {}s", gameItemObjects.size(), gameItemType);
            GameItemService gameItemService = savers.getOptional(gameItemType)
                .orElseThrow(() -> new IllegalStateException("GameItemDao not found for type " + gameItemType));

            List<GameItem> models = gameItemObjects.stream()
                .map(o -> objectMapperWrapper.convertValue(o, gameItemType.getModelType()))
                .collect(Collectors.toList());
            gameItemService.save(models);
        } catch (Exception e) {
            log.error("Failed to save gameItem {}", gameItemType, e);
        }
    }
}
