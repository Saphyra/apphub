package com.github.saphyra.apphub.api.skyxplore.response.game.solar_system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlanetLocationResponse {
    private UUID planetId;
    private String planetName;
    private Coordinate coordinate;
    private UUID owner;
    private String ownerName;
}
