package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
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
public class ProcessService implements GameItemService {
    private final ProcessDao processDao;
    private final ProcessModelValidator processModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        processDao.deleteByGameId(gameId);
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


        processDao.saveAll(models);
    }

    @Override
    public Optional<ProcessModel> findById(UUID id) {
        return processDao.findById(id);
    }

    @Override
    public List<ProcessModel> getByParent(UUID parent) {
        return processDao.getByGameId(parent);
    }

    @Override
    public void deleteById(UUID id) {
        processDao.deleteById(id);
    }
}
