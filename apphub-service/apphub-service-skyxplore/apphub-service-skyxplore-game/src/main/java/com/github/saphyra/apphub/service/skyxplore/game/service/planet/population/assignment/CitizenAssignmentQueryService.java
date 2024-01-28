package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenAssignmentResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider.CitizenAssignmentDataProvider;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CitizenAssignmentQueryService {
    private final List<CitizenAssignmentDataProvider> dataProviders;

    CitizenAssignmentResponse getAssignment(GameData gameData, Process process) {
        Process root = gameData.getProcesses()
            .getRootOf(process);

        Object data = dataProviders.stream()
            .filter(dataProvider -> dataProvider.getType() == root.getType())
            .findAny()
            .map(dataProvider -> dataProvider.getData(gameData, root))
            .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "No CitizenAssignmentDataProvider found for processType " + root.getType()));

        return CitizenAssignmentResponse.builder()
            .type(root.getType().name())
            .data(data)
            .build();
    }
}
