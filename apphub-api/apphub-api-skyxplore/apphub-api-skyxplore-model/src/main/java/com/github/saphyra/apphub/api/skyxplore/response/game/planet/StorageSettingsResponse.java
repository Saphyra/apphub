package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import com.github.saphyra.apphub.api.skyxplore.model.StorageSettingApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StorageSettingsResponse {
    private List<StorageSettingApiModel> currentSettings;
    private List<String> availableResources;
}
