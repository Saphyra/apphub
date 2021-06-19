package com.github.saphyra.apphub.integration.backend.model.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MapResponse {
    private int universeSize;
    private List<MapSolarSystemResponse> solarSystems;
    private List<SolarSystemConnectionResponse> connections;
}
