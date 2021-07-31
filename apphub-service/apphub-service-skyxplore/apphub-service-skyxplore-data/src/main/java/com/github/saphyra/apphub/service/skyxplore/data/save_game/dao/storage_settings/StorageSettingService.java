package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
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
public class StorageSettingService implements GameItemService {
    private final StorageSettingDao storageSettingDao;
    private final StorageSettingModelValidator storageSettingModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
        log.info("Deleting {}s by gameId {}", getClass().getSimpleName(), gameId);
        storageSettingDao.deleteByGameId(gameId);
    }

    @Override
    public GameItemType getType() {
        return GameItemType.STORAGE_SETTING;
    }

    @Override
    public void save(List<GameItem> gameItems) {
        List<StorageSettingModel> models = gameItems.stream()
            .filter(gameItem -> gameItem instanceof StorageSettingModel)
            .map(gameItem -> (StorageSettingModel) gameItem)
            .peek(storageSettingModelValidator::validate)
            .collect(Collectors.toList());

        storageSettingDao.saveAll(models);
    }

    @Override
    public Optional<StorageSettingModel> findById(UUID id) {
        return storageSettingDao.findById(id);
    }

    @Override
    public List<StorageSettingModel> getByParent(UUID parent) {
        return storageSettingDao.getByLocation(parent);
    }

    @Override
    public void deleteById(UUID id) {
        storageSettingDao.deleteById(id);
    }
}
