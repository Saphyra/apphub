package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import com.github.saphyra.apphub.integration.core.ForRemoval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ForRemoval("skyxplore-react")
public class StorageSettingsResponse {
    private List<StorageSettingModel> currentSettings;
    private List<String> availableResources;
}
