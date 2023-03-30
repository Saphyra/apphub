package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class Citizens extends Vector<Citizen> {
    public Citizen findByCitizenIdValidated(UUID citizenId) {
        return stream()
            .filter(citizen -> citizen.getCitizenId().equals(citizenId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Citizen not found by citizenId " + citizenId));
    }

    public List<Citizen> getByLocation(UUID location) {
        return stream()
            .filter(citizen -> citizen.getLocation().equals(location))
            .collect(Collectors.toList());
    }
}
