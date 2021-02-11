package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Surface {
    private final UUID surfaceId;
    private final UUID planetId; //TODO fill in factory
    private final Coordinate coordinate;
    private SurfaceType surfaceType;
    private Building building;
}
