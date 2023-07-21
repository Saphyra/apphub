package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import com.github.saphyra.apphub.integration.structure.api.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SolarSystemConnectionResponse {
    private Coordinate a;
    private Coordinate b;
}
