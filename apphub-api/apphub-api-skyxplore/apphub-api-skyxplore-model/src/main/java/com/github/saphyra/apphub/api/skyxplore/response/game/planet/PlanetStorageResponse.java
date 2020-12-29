package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlanetStorageResponse {
    private StorageTypeResponse energy;
    private StorageTypeResponse liquid;
    private StorageTypeResponse bulk;
}
