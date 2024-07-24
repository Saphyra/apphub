package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettingFactory {
    private final IdGenerator idGenerator;

    public Setting create(UUID gameId, UUID userId, SettingType type, UUID location) {
        return Setting.builder()
            .settingId(idGenerator.randomUuid())
            .gameId(gameId)
            .userId(userId)
            .type(type)
            .location(location)
            .build();
    }
}
