package com.github.saphyra.apphub.service.skyxplore.game.tick.planet;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.PlanetTickTaskCollector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PlanetTickProcessorTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private PlanetTickTaskCollector tickTaskCollector;

    @Mock
    private ErrorReporterService errorReporterService;

    private PlanetTickProcessor underTest;

    @Mock
    private TickTask tickTask1;

    @Mock
    private TickTask tickTask2;

    @Mock
    private Planet planet;

    @Before
    public void setUp() {
        underTest = new PlanetTickProcessor(List.of(tickTaskCollector), errorReporterService);
    }

    @Test
    public void process() {
        given(tickTaskCollector.getTasks(GAME_ID, planet)).willReturn(List.of(tickTask1, tickTask2));

        given(tickTask1.getPriority()).willReturn(1);
        given(tickTask2.getPriority()).willReturn(2);

        doThrow(new RuntimeException()).when(tickTask2).process();

        underTest.processForPlanet(GAME_ID, planet);

        verify(errorReporterService, times(1)).report(anyString(), any());
        verify(tickTask1).process();
        verify(tickTask2).process();
    }
}