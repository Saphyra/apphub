package com.github.saphyra.apphub.integration.structure.api.skyxplore.game;

import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlanetStorageResponse {
    private StorageDetailsResponse energy;
    private StorageDetailsResponse liquid;
    private StorageDetailsResponse bulk;
}
