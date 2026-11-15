package com.github.saphyra.apphub.ci.ui.progress;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class NoOpProgressIndicator implements ProgressIndicator {
    @Override
    public void started(String name) {
        log.debug("Startup initiated: {}", name);
    }

    @Override
    public void completed(String name) {
        log.debug("Startup completed: {}", name);
    }

    @Override
    public void scheduleShutdown() {
        log.debug("Shutdown scheduled");
    }

    @Override
    public void failed(String name) {
        log.debug("Startup failed: {}", name);
    }
}
