package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StorageSettingsResponse {
    private List<StorageSettingModel> currentSettings;
    private List<String> availableResources;
    private Map<String, Integer> availableStorage;
}
