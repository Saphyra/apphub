package com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface;

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
    private final UUID planetId;
    private SurfaceType surfaceType;
}
