package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemConnectionService implements GameItemService {
    private final SystemConnectionDao systemConnectionDao;
    private final SystemConnectionModelValidator systemConnectionModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        systemConnectionDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.SYSTEM_CONNECTION;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<SystemConnectionModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof SystemConnectionModel)
            .map(gameItem -> (SystemConnectionModel) gameItem)
            .peek(systemConnectionModelValidator::validate)
            .collect(Collectors.toList());

        systemConnectionDao.saveAll(models);
    }
}
