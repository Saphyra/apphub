package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
class ConstructionService implements GameItemService {
    private final ConstructionDao constructionDao;
    private final ConstructionModelValidator constructionModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        constructionDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.CONSTRUCTION;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<ConstructionModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof ConstructionModel)
            .map(gameItem -> (ConstructionModel) gameItem)
            .peek(constructionModelValidator::validate)
            .collect(Collectors.toList());

        constructionDao.saveAll(models);
    }

    @Override
    public Optional<ConstructionModel> findById(UUID id) {
        return constructionDao.findById(id);
    }

    @Override
    public List<ConstructionModel> getByParent(UUID parent) {
        return constructionDao.getByExternalReference(parent);
    }

    @Override
    public void deleteById(UUID id) {
        constructionDao.deleteById(id);
    }
}
