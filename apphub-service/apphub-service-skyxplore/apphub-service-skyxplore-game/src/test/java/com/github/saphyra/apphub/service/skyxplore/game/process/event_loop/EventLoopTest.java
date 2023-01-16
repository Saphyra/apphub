package com.github.saphyra.apphub.service.skyxplore.game.process.event_loop;

import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventLoopTest {
    private static final Object OBJECT = "obj";

    @Mock
    private ExecutorServiceBeanFactory executorServiceBeanFactory;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    private EventLoop underTest;

    @Mock
    private Runnable runnable;

    @Mock
    private Callable<Object> callable;

    @Mock
    private Future<ExecutionResult<Void>> voidFuture;

    @Mock
    private Future<ExecutionResult<Object>> objectFuture;

    @Mock
    private SyncCache syncCache;

    @Captor
    private ArgumentCaptor<Callable<Object>> callableArgumentCaptor;

    @BeforeEach
    public void setUp() {
        given(executorServiceBeanFactory.create(any())).willReturn(executorServiceBean);

        underTest = new EventLoop(executorServiceBeanFactory);
    }

    @Test
    public void process() {
        given(executorServiceBean.execute(runnable)).willReturn(voidFuture);

        Future<ExecutionResult<Void>> result = underTest.process(runnable);

        assertThat(result).isEqualTo(voidFuture);
    }

    @Test
    public void processWithSyncCache() {
        given(executorServiceBean.execute(any())).willReturn(voidFuture);

        Future<ExecutionResult<Void>> result = underTest.process(runnable, syncCache);

        assertThat(result).isEqualTo(voidFuture);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceBean).execute(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(syncCache).process();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void processWithResponse() throws Exception {
        given(executorServiceBean.asyncProcess(any(Callable.class))).willReturn(objectFuture);
        given(callable.call()).willReturn(OBJECT);


        Future<ExecutionResult<Object>> result = underTest.processWithResponse(callable, syncCache);

        assertThat(result).isEqualTo(objectFuture);

        verify(executorServiceBean).asyncProcess(callableArgumentCaptor.capture());
        Object resultObject = callableArgumentCaptor.getValue()
            .call();
        assertThat(resultObject).isEqualTo(OBJECT);

        verify(syncCache).process();
    }

    @Test
    public void stop() {
        underTest.stop();

        verify(executorServiceBean).stop();
    }
}