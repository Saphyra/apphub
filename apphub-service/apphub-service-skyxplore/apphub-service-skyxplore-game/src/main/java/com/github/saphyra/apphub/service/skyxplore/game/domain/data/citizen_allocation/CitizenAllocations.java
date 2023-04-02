package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

public class CitizenAllocations extends Vector<CitizenAllocation> {
    public CitizenAllocation findByProcessIdValidated(UUID processId) {
        return findByProcessId(processId)
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "CitizenAllocation not found by processId " + processId));
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
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Citizen not found by citizenId " + citizenId));
    }

    public Optional<CitizenAllocation> findByCitizenId(UUID citizenId) {
        return stream()
            .filter(citizenAllocation -> citizenAllocation.getCitizenId().equals(citizenId))
            .findAny();
    }
}
