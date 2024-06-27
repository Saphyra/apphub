package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.data.common.GameProxy;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.Setting;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateOrUpdateSettingService {
    private final SettingValidator settingValidator;
    private final GameProxy gameProxy;
    private final SettingDao settingDao;
    private final SettingFactory settingFactory;
    private final ObjectMapperWrapper objectMapperWrapper;

    public void createOrUpdate(UUID userId, SettingModel request) {
        settingValidator.validate(request);

        UUID gameId = gameProxy.getGameIdValidated(userId);

        Setting setting = getOrCreate(gameId, userId, request.getType(), request.getLocation());

        setting.setData(objectMapperWrapper.writeValueAsString(request.getData()));

        settingDao.save(setting);
    }

    private Setting getOrCreate(UUID gameId, UUID userId, SettingType type, UUID location) {
        return settingDao.findByUserIdAndGameIdAndTypeAndLocation(userId, gameId, type, location)
            .orElseGet(() -> settingFactory.create(gameId, userId, type, location));
    }
}
