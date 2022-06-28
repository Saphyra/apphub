package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageCacheTest {
    private static final UUID RECIPIENT = UUID.randomUUID();

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @InjectMocks
    private MessageCache underTest;

    @Mock
    private Runnable task;

    @Mock
    private Object id;

    @Test
    public void process() {
        underTest.add(RECIPIENT, WebSocketEventName.SKYXPLORE_GAME_PAUSED, id, task);

        underTest.process();

        verify(executorServiceBean).execute(task);
    }
}