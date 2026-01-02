package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.service.skyxplore.data.common.GameProxy;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.Setting;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingQueryService {
    private final SettingDao settingDao;
    private final GameProxy gameProxy;
    private final ObjectMapper objectMapper;

    public SettingModel getSetting(UUID userId, SettingIdentifier request) {
        UUID gameId = gameProxy.getGameIdValidated(userId);

        Setting setting = settingDao.findByUserIdAndGameIdAndTypeAndLocation(userId, gameId, request.getType(), request.getLocation())
            .orElseGet(() -> settingDao.findByUserIdAndGameIdAndTypeAndLocation(userId, gameId, request.getType(), null).orElse(null));

        if (!isNull(setting)) {
            return SettingModel.builder()
                .type(setting.getType())
                .location(setting.getLocation())
                .data(objectMapper.readValue(setting.getData(), Object.class))
                .build();
        }

        return null;
    }
}
