package com.github.saphyra.apphub.ci.ui.startup;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class NoOpStartupIndicator implements StartupIndicator {
    @Override
    public void startupInitiated(String name) {
        log.debug("Startup initiated: {}", name);
    }

    @Override
    public void startupCompleted(String name) {
        log.debug("Startup completed: {}", name);
    }

    @Override
    public void scheduleShutdown() {
        log.debug("Shutdown scheduled");
    }
}
