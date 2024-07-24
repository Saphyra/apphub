package com.github.saphyra.apphub.service.skyxplore.data.setting;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SettingProperties {
    @Value("${setting.maxDataSize}")
    private Integer maxDataSize;
}
