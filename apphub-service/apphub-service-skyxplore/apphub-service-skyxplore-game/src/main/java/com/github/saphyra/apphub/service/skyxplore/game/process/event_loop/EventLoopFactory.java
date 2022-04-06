package com.github.saphyra.apphub.service.skyxplore.game.process.event_loop;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventLoopFactory {
    private final ExecutorServiceBeanFactory executorServiceBeanFactory;

    public EventLoop create() {
        return EventLoop.builder()
            .executorServiceBeanFactory(executorServiceBeanFactory)
            .build();
    }
}
