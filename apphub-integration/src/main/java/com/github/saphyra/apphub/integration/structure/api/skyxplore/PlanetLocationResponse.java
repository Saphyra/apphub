package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import com.github.saphyra.apphub.integration.structure.api.Coordinate;
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
