package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreDataGameController;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
@Slf4j
public class SkyXploreDataGameControllerImpl implements SkyXploreDataGameController {
    private final OptionalMap<GameItemType, GameItemSaver> savers;
    private final ObjectMapperWrapper objectMapperWrapper;

    public SkyXploreDataGameControllerImpl(List<GameItemSaver> savers, ObjectMapperWrapper objectMapperWrapper) {
        this.savers = new OptionalHashMap<>(savers.stream()
            .collect(Collectors.toMap(GameItemSaver::getType, Function.identity())));
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @Override
    //TODO unit test
    //TODO int test
    public void saveGameData(List<Object> items) {
        List<BiWrapper<Object, GameItem>> gameItems = items.stream()
            .map(o -> new BiWrapper<>(o, objectMapperWrapper.convertValue(o, GameItem.class)))
            .collect(Collectors.toList());

        if (log.isDebugEnabled()) {
            log.debug("saveGameData request arrived with size {} and types {}", items.size(), gameItems.stream().map(BiWrapper::getEntity2).map(GameItem::getType).map(Enum::name).collect(Collectors.joining(", ")));
        }
        gameItems.stream()
            .parallel()
            .forEach(biWrapper -> save(biWrapper.getEntity1(), biWrapper.getEntity2()));
    }

    private void save(Object gameItemObject, GameItem gameItem) {
        try {
            if (isNull(gameItem.getType())) {
                throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "type", "must not be null"), "type must not be null");
            }

            GameItemSaver gameItemSaver = savers.getOptional(gameItem.getType())
                .orElseThrow(() -> new IllegalStateException("GameItemDao not found for type " + gameItem.getType()));

            log.debug("GameItem: {}", gameItem);
            GameItem model = objectMapperWrapper.convertValue(gameItemObject, gameItem.getType().getModelType());
            log.debug("Model: {}", model);
            gameItemSaver.save(model);
        } catch (Exception e) {
            log.error("Failed to save gameItem {} with id {}", gameItem.getType(), gameItem.getId(), e);
        }
    }
}
