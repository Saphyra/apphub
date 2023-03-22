package com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority;

import java.util.UUID;
import java.util.Vector;

public class Priorities extends Vector<Priority> {
    public Priority getByLocationAndType(UUID location, PriorityType type) {
        return stream()
            .filter(priority -> priority.getLocation().equals(location))
            .filter(priority -> priority.getType() == type)
            .findFirst()
            .orElseThrow();
    }
}
