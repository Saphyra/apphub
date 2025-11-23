package com.github.saphyra.apphub.ci.ui.startup;

public interface StartupIndicator {
    void startupInitiated(String name);

    void startupCompleted(String name);

    void scheduleShutdown();
}
