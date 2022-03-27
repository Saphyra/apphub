package com.github.saphyra.apphub.service.skyxplore.game.domain.process;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageCacheTest {
    private static final UUID RECIPIENT = UUID.randomUUID();

    @Mock
    private ExecutorServiceBean executorServiceBean;

    private final MessageCache underTest = new MessageCache();

    @Mock
    private Runnable task;

    @Mock
    private Object id;

    @Test
    public void process() {
        underTest.add(RECIPIENT, WebSocketEventName.SKYXPLORE_GAME_PAUSED, id, task);

        underTest.process(executorServiceBean);

        verify(executorServiceBean).execute(task);
    }
}