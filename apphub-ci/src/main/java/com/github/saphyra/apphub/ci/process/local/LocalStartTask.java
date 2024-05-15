package com.github.saphyra.apphub.ci.process.local;

import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LocalStartTask implements Runnable {
    private final ServicePinger servicePinger;
    private final Service service;

    @Override
    @SneakyThrows
    public void run() {
        log.info("Starting service {}", service.getName());

        new ProcessBuilder("cmd", "/c", "start", "java", "-Xmx512m", "-Dfile.encoding=UTF-8", "-DSPRING_ACTIVE_PROFILE=local", "-jar", service.getLocation())
            .start();

        servicePinger.pingService(service.getPort())
            .ifPresent(cause -> {
                throw new IllegalStateException(service.getName() + " failed to start.", cause);
            });

        log.info("{} successfully started.", service.getName());
    }
}
