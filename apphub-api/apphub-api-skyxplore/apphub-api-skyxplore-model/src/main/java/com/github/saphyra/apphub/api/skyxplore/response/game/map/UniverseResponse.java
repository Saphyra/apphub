package com.github.saphyra.apphub.api.skyxplore.response.game.map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UniverseResponse {
    private int universeSize;
    private List<MapSolarSystemResponse> solarSystems;
    private List<SolarSystemConnectionResponse> connections;
}
