package com.github.saphyra.apphub.service.skyxplore.game.process.event_loop;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
public class EventLoopFactory {
    private final ExecutorServiceBeanFactory executorServiceBeanFactory;
    private final ExecutorServiceBean executorServiceBean;
    private final GameDataProxy gameDataProxy;

    public EventLoop create() {
        return EventLoop.builder()
            .executorServiceBeanFactory(executorServiceBeanFactory)
            .gameDataProxy(gameDataProxy)
            .generalExecutor(executorServiceBean)
            .build();
    }
}
