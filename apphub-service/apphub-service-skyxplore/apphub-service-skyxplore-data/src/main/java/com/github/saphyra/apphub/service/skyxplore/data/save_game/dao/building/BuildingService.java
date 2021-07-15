package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildingService implements GameItemService {
    private final BuildingDao buildingDao;
    private final BuildingModelValidator buildingModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        buildingDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.BUILDING;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<BuildingModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof BuildingModel)
            .map(gameItem -> (BuildingModel) gameItem)
            .peek(buildingModelValidator::validate)
            .collect(Collectors.toList());

        buildingDao.saveAll(models);
    }

    @Override
    public Optional<BuildingModel> findById(UUID id) {
        return buildingDao.findById(id);
    }

    @Override
    public List<BuildingModel> getByParent(UUID parent) {
        return buildingDao.findBySurfaceId(parent)
            .map(Arrays::asList)
            .orElse(Collections.emptyList());
    }
}
