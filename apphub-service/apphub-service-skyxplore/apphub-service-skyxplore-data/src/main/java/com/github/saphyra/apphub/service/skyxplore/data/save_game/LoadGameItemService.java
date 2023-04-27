package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class LoadGameItemService {
    private final List<GameItemService> gameItemServices;
    private final ErrorReporterService errorReporterService;
    private final Integer itemsPerPage;

    @Builder
    public LoadGameItemService(
        List<GameItemService> gameItemServices,
        ErrorReporterService errorReporterService,
        @Value("${game.load.itemsPerPage}") Integer itemsPerPage
    ) {
        this.gameItemServices = gameItemServices;
        this.errorReporterService = errorReporterService;
        this.itemsPerPage = itemsPerPage;
    }

    public List<? extends GameItem> loadPageForGameItems(UUID gameId, Integer page, GameItemType type) {
        Optional<GameItemService> gameItemService = findService(type);

        if (gameItemService.isPresent()) {
            return gameItemService.get()
                .loadPage(gameId, page, itemsPerPage);
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
