package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.tick.planet.processor.construction.ConstructionTickProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionTickTaskTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ConstructionTickProcessor constructionTickProcessor;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Test
    public void process() {
        ConstructionTickTask.builder()
            .gameId(GAME_ID)
            .planet(planet)
            .surface(surface)
            .constructionTickProcessor(constructionTickProcessor)
            .build()
            .process();

        verify(constructionTickProcessor).process(GAME_ID, planet, surface);
    }
}