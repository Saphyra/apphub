package com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview;

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
