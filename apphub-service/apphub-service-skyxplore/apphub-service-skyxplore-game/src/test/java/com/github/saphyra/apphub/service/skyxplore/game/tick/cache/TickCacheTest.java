package com.github.saphyra.apphub.service.skyxplore.game.tick.cache;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TickCacheTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @InjectMocks
    private TickCache underTest;

    @Mock
    private TickCacheItem tickCacheItem;

    @Test
    public void process() {
        underTest.put(GAME_ID, tickCacheItem);

        underTest.process(GAME_ID);

        verify(tickCacheItem).process(gameDataProxy, executorServiceBean);
    }
}