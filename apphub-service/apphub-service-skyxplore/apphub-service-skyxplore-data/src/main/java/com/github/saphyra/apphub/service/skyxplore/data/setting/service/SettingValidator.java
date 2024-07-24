package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.data.setting.SettingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class SettingValidator {
    private final SettingProperties settingProperties;

    void validate(SettingModel request) {
        ValidationUtil.notNull(request.getType(), "type");
        if (!isNull(request.getData())) {
            ValidationUtil.maxLength(request.getData().toString(), settingProperties.getMaxDataSize(), "data");
        }
    }
}
