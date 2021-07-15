package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
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
public class CoordinateService implements GameItemService {
    private final CoordinateDao coordinateDao;
    private final CoordinateModelValidator coordinateModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        coordinateDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.COORDINATE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<CoordinateModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof CoordinateModel)
            .map(gameItem -> (CoordinateModel) gameItem)
            .peek(coordinateModelValidator::validate)
            .collect(Collectors.toList());

        coordinateDao.saveAll(models);
    }

    @Override
    public Optional<CoordinateModel> findById(UUID id) {
        return coordinateDao.findById(id);
    }

    @Override
    public List<CoordinateModel> getByParent(UUID parent) {
        return coordinateDao.getByReferenceId(parent);
    }
}
