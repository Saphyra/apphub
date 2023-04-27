package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
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
public class ProcessService implements GameItemService {
    private final ProcessDao dao;
    private final ProcessModelValidator processModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        dao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.PROCESS;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<ProcessModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof ProcessModel)
            .map(gameItem -> (ProcessModel) gameItem)
            .peek(processModelValidator::validate)
            .collect(Collectors.toList());


        dao.saveAll(models);
    }

    @Override
    public void deleteById(UUID id) {
        dao.deleteById(id);
    }

    @Override
    public List<ProcessModel> loadPage(UUID gameId, Integer page, Integer itemsPerPage) {
        return dao.getPageByGameId(gameId, page, itemsPerPage);
    }
}
