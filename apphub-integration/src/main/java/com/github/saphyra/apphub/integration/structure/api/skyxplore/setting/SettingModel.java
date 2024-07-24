package com.github.saphyra.apphub.integration.structure.api.skyxplore.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SettingModel {
    private UUID location;
    private SettingType type;
    private Object data;
}
