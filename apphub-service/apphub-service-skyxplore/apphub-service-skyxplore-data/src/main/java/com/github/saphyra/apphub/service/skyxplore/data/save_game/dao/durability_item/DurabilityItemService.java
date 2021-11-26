package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
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
public class DurabilityItemService implements GameItemService {
    private final DurabilityItemDao durabilityItemDao;
    private final DurabilityItemValidator durabilityItemValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        durabilityItemDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.DURABILITY_ITEM_MODEL;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<DurabilityItemModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof DurabilityItemModel)
            .map(gameItem -> (DurabilityItemModel) gameItem)
            .peek(durabilityItemValidator::validate)
            .collect(Collectors.toList());

        durabilityItemDao.saveAll(models);
    }

    @Override
    public Optional<DurabilityItemModel> findById(UUID id) {
        return durabilityItemDao.findById(id);
    }

    @Override
    public List<DurabilityItemModel> getByParent(UUID parent) {
        return durabilityItemDao.getByParent(parent);
    }

    @Override
    public void deleteById(UUID id) {
        durabilityItemDao.deleteById(id);
    }
}
