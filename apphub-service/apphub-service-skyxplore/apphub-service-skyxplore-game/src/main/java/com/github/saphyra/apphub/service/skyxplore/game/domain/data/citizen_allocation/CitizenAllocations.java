package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class CitizenAllocations extends Vector<CitizenAllocation> {
    public CitizenAllocation findByProcessIdValidated(UUID processId) {
        return findByProcessId(processId)
            .orElseThrow();
    }

    public Optional<CitizenAllocation> findByProcessId(UUID processId) {
        return stream()
            .filter(citizenAllocation -> citizenAllocation.getProcessId().equals(processId))
            .findAny();
    }

    public void deleteByProcessId(UUID processId) {
        removeIf(citizenAllocation -> citizenAllocation.getProcessId().equals(processId));
    }

    public CitizenAllocation findByCitizenIdValidated(UUID citizenId) {
        return findByCitizenId(citizenId)
            .orElseThrow();
    }

    public Optional<CitizenAllocation> findByCitizenId(UUID citizenId) {
        return stream()
            .filter(citizenAllocation -> citizenAllocation.getCitizenId().equals(citizenId))
            .findAny();
    }
}
