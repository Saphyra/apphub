package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
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
public class PriorityService implements GameItemService {
    private final PriorityDao priorityDao;
    private final PriorityModelValidator priorityModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        priorityDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.PRIORITY;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<PriorityModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof PriorityModel)
            .map(gameItem -> (PriorityModel) gameItem)
            .peek(priorityModelValidator::validate)
            .collect(Collectors.toList());

        priorityDao.saveAll(models);
    }

    @Override
    public Optional<PriorityModel> findById(UUID id) {
        throw ExceptionFactory.reportedException("Priority cannot be loaded by id.");
    }

    @Override
    public List<PriorityModel> getByParent(UUID parent) {
        return priorityDao.getByLocation(parent);
    }

    @Override
    public void deleteById(UUID id) {
        throw new UnsupportedOperationException("Priority cannot be deleted by uuid");
    }
}
