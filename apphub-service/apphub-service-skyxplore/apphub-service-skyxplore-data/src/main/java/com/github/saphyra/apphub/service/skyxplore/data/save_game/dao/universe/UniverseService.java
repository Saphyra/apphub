package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
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
public class UniverseService implements GameItemService {
    private final UniverseDao universeDao;
    private final UniverseModelValidator universeModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        deleteById(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.UNIVERSE;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<UniverseModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof UniverseModel)
            .map(gameItem -> (UniverseModel) gameItem)
            .peek(universeModelValidator::validate)
            .collect(Collectors.toList());


        universeDao.saveAll(models);
    }

    @Override
    public Optional<UniverseModel> findById(UUID id) {
        return universeDao.findById(id);
    }

    @Override
    public List<UniverseModel> getByParent(UUID parent) {
        return universeDao.findById(parent)
            .map(Arrays::asList)
            .orElse(Collections.emptyList());
    }

    @Override
    public void deleteById(UUID id) {
        universeDao.deleteById(id);
    }
}