package com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class Priorities extends Vector<Priority> {
    public Priority findByLocationAndType(UUID location, PriorityType type) {
        return stream()
            .filter(priority -> priority.getLocation().equals(location))
            .filter(priority -> priority.getType() == type)
            .findFirst()
            .orElseThrow();
    }

    public List<Priority> getByLocation(UUID location) {
        return stream()
            .filter(priority -> priority.getLocation().equals(location))
            .toList();
    }
}
