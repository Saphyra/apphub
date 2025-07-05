package com.github.saphyra.apphub.ci.process.local;

import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Service;
import com.google.common.base.Stopwatch;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class LocalStartTask implements Runnable {
    private final ServicePinger servicePinger;
    private final Service service;
    @Builder.Default
    private final String activeProfiles = Constants.PROFILE_LOCAL;
    @Builder.Default
    private final String protocol = Constants.PROTOCOL_UNSECURE;

    @Override
    @SneakyThrows
    public void run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Starting service {}", service.getName());

        new ProcessBuilder("cmd", "/c", "start", "java", "-Xmx1024m", "-Dfile.encoding=UTF-8", "-DSPRING_ACTIVE_PROFILE=%s".formatted(activeProfiles), "-jar", service.getLocation())
            .start();

        servicePinger.pingLocal(Optional.ofNullable(service.getHealthCheckPort()).orElse(service.getPort()), protocol)
            .ifPresent(cause -> {
                throw new IllegalStateException(service.getName() + " failed to start.", cause);
            });

        stopwatch.stop();
        log.info("{} successfully started in {}s.", service.getName(), (stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d));
    }
}
