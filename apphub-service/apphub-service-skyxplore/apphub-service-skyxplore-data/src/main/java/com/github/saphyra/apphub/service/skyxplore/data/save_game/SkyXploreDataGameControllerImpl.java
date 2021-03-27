package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreDataGameController;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
@Slf4j
public class SkyXploreDataGameControllerImpl implements SkyXploreDataGameController {
    private final OptionalMap<GameItemType, GameItemService> savers;
    private final ObjectMapperWrapper objectMapperWrapper;

    public SkyXploreDataGameControllerImpl(List<GameItemService> savers, ObjectMapperWrapper objectMapperWrapper) {
        this.savers = new OptionalHashMap<>(savers.stream()
            .collect(Collectors.toMap(GameItemService::getType, Function.identity())));
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @Override
    public void saveGameData(List<Object> items) {
        log.info("Saving {} number of gameItems...", items.size());
        items.stream()
            .map(o -> new BiWrapper<>(o, objectMapperWrapper.convertValue(o, GameItem.class).getType()))
            .filter(this::isTypeFilled)
            .collect(Collectors.groupingBy(BiWrapper::getEntity2))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(BiWrapper::getEntity1).collect(Collectors.toList())))
            .forEach(this::save);
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
