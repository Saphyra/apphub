package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest.RestProcess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RestCitizenAssignmentDataProviderTest {
    private static final Integer RESTED_FOR_TICKS = 23;
    private static final Integer REST_FOR_TICKS = 32;

    @InjectMocks
    private RestCitizenAssignmentDataProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private RestProcess process;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.REST);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getData() {
        given(process.getRestedForTicks()).willReturn(RESTED_FOR_TICKS);
        given(process.getRestForTicks()).willReturn(REST_FOR_TICKS);

        Map<String, Integer> result = (Map<String, Integer>) underTest.getData(gameData, process);

        assertThat(result).containsEntry("restForTicks", REST_FOR_TICKS);
        assertThat(result).containsEntry("restedForTicks", RESTED_FOR_TICKS);
    }
}