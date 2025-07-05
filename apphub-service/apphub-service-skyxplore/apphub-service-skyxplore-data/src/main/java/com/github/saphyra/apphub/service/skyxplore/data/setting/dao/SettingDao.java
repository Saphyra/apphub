package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SettingDao extends AbstractDao<SettingEntity, Setting, String, SettingRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    SettingDao(SettingConverter converter, SettingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<Setting> findByUserIdAndGameIdAndTypeAndLocation(UUID userId, UUID gameId, SettingType type, UUID location) {
        return converter.convertEntity(repository.findByUserIdAndGameIdAndTypeAndLocation(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(gameId),
            type,
            uuidConverter.convertDomain(location)
        ));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteByGameId(UUID gameId) {
        repository.deleteByGameId(uuidConverter.convertDomain(gameId));
    }
}
