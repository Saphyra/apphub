package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameItemLoader {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final GameDataProxy gameDataProxy;

    public <T extends GameItem> Optional<T> loadItem(UUID id, GameItemType type) {
        //noinspection unchecked
        return Optional.ofNullable(gameDataProxy.loadItem(id, type))
            .filter(gameItem1 -> gameItem1.getType() == type)
            .map(item -> (T) objectMapperWrapper.convertValue(item, type.getModelType()));
    }

    public <T extends GameItem> List<T> loadChildren(UUID parent, GameItemType type) {
        //noinspection unchecked
        return gameDataProxy.loadChildren(parent, type)
            .stream()
            .filter(gameItem1 -> gameItem1.getType() == type)
            .map(item -> (T) objectMapperWrapper.convertValue(item, type.getModelType()))
            .collect(Collectors.toList());
    }
}
