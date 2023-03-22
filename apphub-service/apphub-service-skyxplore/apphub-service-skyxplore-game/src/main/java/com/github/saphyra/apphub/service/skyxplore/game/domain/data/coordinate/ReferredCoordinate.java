package com.github.saphyra.apphub.service.skyxplore.game.domain.data.coordinate;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ReferredCoordinate {
    private final UUID referenceId;
    private Coordinate coordinate;
}
