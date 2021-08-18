package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
class LoadGameItemService {
    private final List<GameItemService> gameItemServices;
    private final ErrorReporterService errorReporterService;

    public GameItem loadGameItem(UUID id, GameItemType type) {
        Optional<GameItemService> gameItemService = findService(type);

        if (gameItemService.isPresent()) {
            return gameItemService.get()
                .findById(id)
                .orElse(null);
        } else {
            errorReporterService.report("No gameItemService found for type " + type);
            return null;
        }
    }

    public List<? extends GameItem> loadChildrenOfGameItem(UUID parent, GameItemType type) {
        Optional<GameItemService> gameItemService = findService(type);

        if (gameItemService.isPresent()) {
            return gameItemService.get()
                .getByParent(parent);
        } else {
            errorReporterService.report("No gameItemService found for type " + type);
            return Collections.emptyList();
        }
    }

    private Optional<GameItemService> findService(GameItemType type) {
        return gameItemServices.stream()
            .filter(service -> type == service.getType())
            .findFirst();
    }
}
