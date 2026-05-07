package com.github.saphyra.apphub.ci.task_queue;

import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;

@Component
public class TaskQueue extends LinkedBlockingDeque<Runnable> {
}
