package com.github.saphyra.apphub.ci.process.local;

import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Service;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class LocalStartTask implements Runnable {
    private final ServicePinger servicePinger;
    private final Service service;

    @Override
    @SneakyThrows
    public void run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Starting service {}", service.getName());

        new ProcessBuilder("cmd", "/c", "start", "java", "-Xmx512m", "-Dfile.encoding=UTF-8", "-DSPRING_ACTIVE_PROFILE=local", "-jar", service.getLocation())
            .start();

        servicePinger.pingLocal(service.getPort())
            .ifPresent(cause -> {
                throw new IllegalStateException(service.getName() + " failed to start.", cause);
            });

        stopwatch.stop();
        log.info("{} successfully started in {}s.", service.getName(), (stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d));
    }
}
