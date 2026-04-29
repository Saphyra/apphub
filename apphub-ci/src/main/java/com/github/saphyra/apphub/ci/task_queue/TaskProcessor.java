package com.github.saphyra.apphub.ci.task_queue;

import com.github.saphyra.apphub.ci.utils.concurrent.ExecutorServiceBean;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class TaskProcessor {
    private final TaskQueue taskQueue;
    private final ExecutorServiceBean executorServiceBean;

    @PostConstruct
    void start() {
        executorServiceBean.execute(() -> {
            while (true) {
                try {
                    Runnable task = taskQueue.takeFirst();

                    task.run();
                } catch (Exception e) {
                    log.error("Failed processing task", e);
                }
            }
        });
    }
}
