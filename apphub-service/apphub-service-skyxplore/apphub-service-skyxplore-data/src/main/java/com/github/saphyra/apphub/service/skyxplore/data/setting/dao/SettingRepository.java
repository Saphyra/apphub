package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface SettingRepository extends CrudRepository<SettingEntity, String> {
    Optional<SettingEntity> findByUserIdAndGameIdAndTypeAndLocation(String userId, String gameId, SettingType type, String location);

    void deleteByUserId(String userId);

    void deleteByGameId(String gameId);
}
