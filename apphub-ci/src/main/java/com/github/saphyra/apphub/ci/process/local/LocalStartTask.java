package com.github.saphyra.apphub.ci.process.local;

import com.github.saphyra.apphub.ci.ui.startup.StartupIndicator;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Service;
import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalStartTask implements Runnable {
    private final ServicePinger servicePinger;
    private final Service service;
    private final StartupIndicator startupIndicator;
    private final Map<String, String> arguments = new HashMap<>();

    public LocalStartTask(ServicePinger servicePinger, Service service, StartupIndicator startupIndicator) {
        this(servicePinger, service, Constants.PROFILE_LOCAL, startupIndicator);
    }

    public LocalStartTask(ServicePinger servicePinger, Service service, String activeProfiles, StartupIndicator startupIndicator) {
        this(servicePinger, service, activeProfiles, startupIndicator, new HashMap<>());
    }

    public LocalStartTask(ServicePinger servicePinger, Service service, StartupIndicator startupIndicator, Map<String, String> properties) {
        this(servicePinger, service, Constants.PROFILE_LOCAL, startupIndicator, properties);
    }

    public LocalStartTask(ServicePinger servicePinger, Service service, String activeProfiles, StartupIndicator startupIndicator, Map<String, String> arguments) {
        this.servicePinger = servicePinger;
        this.service = service;
        this.startupIndicator = startupIndicator;
        this.arguments.putAll(arguments);
        this.arguments.put("SPRING_ACTIVE_PROFILE", activeProfiles);
    }

    @Override
    @SneakyThrows
    public void run() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Starting service {}", service.getName());

        List<String> command = new ArrayList<>();
        command.addAll(List.of(
            "cmd",
            "/c",
            "start",
            "java",
            "-Xmx1024m",
            "-Dfile.encoding=UTF-8"
        ));
        command.addAll(getArguments());
        command.addAll(List.of(
            "-jar",
            service.getLocation()
        ));

        new ProcessBuilder(command)
            .start();

        startupIndicator.startupInitiated(service.getName());

        servicePinger.pingLocal(Optional.ofNullable(service.getHealthCheckPort()).orElse(service.getPort()))
            .ifPresent(cause -> {
                throw new IllegalStateException(service.getName() + " failed to start.", cause);
            });

        stopwatch.stop();
        startupIndicator.startupCompleted(service.getName());
        log.info("{} successfully started in {}s.", service.getName(), (stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d));
    }

    private Collection<String> getArguments() {
        return arguments.entrySet()
            .stream()
            .map(entry -> "-D%s=%s".formatted(entry.getKey(), entry.getValue()))
            .toList();
    }
}
