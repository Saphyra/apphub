package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingsModel;
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
    private List<StorageSettingsModel> currentSettings;
    private List<String> availableResources;
    private Map<String, Integer> availableStorage;
}