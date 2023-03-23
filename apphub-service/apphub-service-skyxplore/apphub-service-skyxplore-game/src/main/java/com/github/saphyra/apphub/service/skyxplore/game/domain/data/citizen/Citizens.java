package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

//TODO unit test
public class Citizens extends Vector<Citizen> {
    public Citizen findByCitizenIdValidated(UUID citizenId) {
        return stream()
            .filter(citizen -> citizen.getCitizenId().equals(citizenId))
            .findAny()
            .orElseThrow();
    }

    public List<Citizen> getByLocation(UUID location) {
        return stream()
            .filter(citizen -> citizen.getLocation().equals(location))
            .collect(Collectors.toList());
    }
}
