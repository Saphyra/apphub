package com.github.saphyra.apphub.api.skyxplore.response.game.map;

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
public class MapSolarSystemResponse {
    private UUID solarSystemId;
    private String solarSystemName;
    private int planetNum;
    private Coordinate coordinate;
}
