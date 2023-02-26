package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RequestWorkProcessFactoryForDeconstructionTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer PARALLEL_WORKERS = 234;
    private static final Integer REQUIRED_WORK_POINTS = 435;

    @Mock
    private RequestWorkProcessFactory requestWorkProcessFactory;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private RequestWorkProcessFactoryForDeconstruction underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private DeconstructionProperties deconstructionProperties;

    @Mock
    private RequestWorkProcess requestWorkProcess;

    @Test
    void createRequestWorkProcess() {
        given(gameProperties.getDeconstruction()).willReturn(deconstructionProperties);
        given(deconstructionProperties.getParallelWorkers()).willReturn(PARALLEL_WORKERS);
        given(deconstructionProperties.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);

        given(requestWorkProcessFactory.create(game, PROCESS_ID, planet, DECONSTRUCTION_ID, RequestWorkProcessType.DECONSTRUCTION, SkillType.BUILDING, REQUIRED_WORK_POINTS, PARALLEL_WORKERS)).willReturn(List.of(requestWorkProcess));

        List<RequestWorkProcess> result = underTest.createRequestWorkProcesses(game, PROCESS_ID, planet, DECONSTRUCTION_ID);

        assertThat(result).containsExactly(requestWorkProcess);
    }
}