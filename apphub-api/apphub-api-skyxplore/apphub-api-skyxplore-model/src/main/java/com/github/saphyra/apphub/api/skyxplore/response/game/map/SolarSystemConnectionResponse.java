package com.github.saphyra.apphub.api.skyxplore.response.game.map;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
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
