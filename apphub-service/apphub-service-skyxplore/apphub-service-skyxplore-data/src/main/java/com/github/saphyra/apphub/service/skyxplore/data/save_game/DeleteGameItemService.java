package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeleteGameItemService {
    private final List<GameItemService> gameItemServices;

    void deleteItem(UUID id, GameItemType type) {
        gameItemServices.stream()
            .filter(gameItemService -> gameItemService.getType() == type)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.reportedException("GameItemService not found for type " + type))
            .deleteById(id);
    }
}
