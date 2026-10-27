package com.github.saphyra.apphub.ci.ui.progress;

public interface ProgressIndicator {
    void started(String name);

    void completed(String name);

    void scheduleShutdown();

    void failed(String name);
}
