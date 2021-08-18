package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import com.github.saphyra.apphub.api.skyxplore.model.game.StorageSettingModel;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StorageSettingDao extends AbstractDao<StorageSettingEntity, StorageSettingModel, String, StorageSettingRepository> {
    private final UuidConverter uuidConverter;

    public StorageSettingDao(StorageSettingConverter converter, StorageSettingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }

    public Optional<StorageSettingModel> findById(UUID storageSettingId) {
        return findById(uuidConverter.convertDomain(storageSettingId));
    }

    public List<StorageSettingModel> getByLocation(UUID location) {
        return converter.convertEntity(repository.getByLocation(uuidConverter.convertDomain(location)));
    }

    public void deleteById(UUID storageSettingId) {
        deleteById(uuidConverter.convertDomain(storageSettingId));
    }
}
