package com.github.saphyra.apphub.service.skyxplore.game.tick.cache;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Assignment {
    private final Citizen citizen;
    private final UUID location;
    private int workPointsLeft;

    public void reduceWorkPoints(int workPointsUsed) {
        workPointsLeft -= workPointsUsed;
    }
}
