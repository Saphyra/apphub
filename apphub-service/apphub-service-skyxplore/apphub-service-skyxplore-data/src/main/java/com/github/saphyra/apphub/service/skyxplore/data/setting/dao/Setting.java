package com.github.saphyra.apphub.service.skyxplore.data.setting.dao;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Setting {
    private final UUID settingId;
    private final UUID gameId;
    private final UUID userId;
    private final SettingType type;
    private final UUID location;
    private String data;
}
