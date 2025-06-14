package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenAssignmentResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider.CitizenAssignmentDataProvider;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CitizenAssignmentQueryServiceTest {
    private static final Object DATA = "data";

    @Mock
    private CitizenAssignmentDataProvider dataProvider;

    private CitizenAssignmentQueryService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Process process;

    @Mock
    private Processes processes;

    @Mock
    private Process root;

    @BeforeEach
    void setUp() {
        underTest = new CitizenAssignmentQueryService(List.of(dataProvider));

        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getRootOf(process)).willReturn(root);
    }

    @Test
    void noMatchingDataProvider() {
        given(root.getType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(dataProvider.getType()).willReturn(ProcessType.DECONSTRUCT_BUILDING_MODULE);

        Throwable ex = catchThrowable(() -> underTest.getAssignment(gameData, process));

        ExceptionValidator.validateReportedException(ex, HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR);
    }

    @Test
    void getAssignment() {
        given(root.getType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(dataProvider.getType()).willReturn(ProcessType.PRODUCTION_ORDER);
        given(dataProvider.getData(gameData, root)).willReturn(DATA);

        CitizenAssignmentResponse result = underTest.getAssignment(gameData, process);

        assertThat(result.getType()).isEqualTo(ProcessType.PRODUCTION_ORDER.name());
        assertThat(result.getData()).isEqualTo(DATA);
    }
}