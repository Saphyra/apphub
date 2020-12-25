package com.github.saphyra.apphub.api.skyxplore.response.game.solar_system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SolarSystemResponse {
    private UUID solarSystemId;
    private int radius;
    private String systemName;
    private List<PlanetLocationResponse> planets;
}
