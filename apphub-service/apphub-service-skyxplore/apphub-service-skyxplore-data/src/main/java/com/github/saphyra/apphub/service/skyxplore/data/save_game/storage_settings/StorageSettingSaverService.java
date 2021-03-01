package com.github.saphyra.apphub.service.skyxplore.data.save_game.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameItemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSettingSaverService implements GameItemSaver {
    private final StorageSettingDao storageSettingDao;
    private final StorageSettingModelValidator storageSettingModelValidator;

    @Override
    public void deleteByGameId(UUID gameId) {
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
}
