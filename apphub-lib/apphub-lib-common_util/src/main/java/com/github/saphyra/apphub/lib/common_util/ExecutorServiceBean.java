package com.github.saphyra.apphub.lib.common_util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorServiceBean implements Executor {
    private final Executor executor = Executors.newCachedThreadPool();


    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }
}
