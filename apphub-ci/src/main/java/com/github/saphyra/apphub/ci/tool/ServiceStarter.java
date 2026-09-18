package com.github.saphyra.apphub.ci.tool;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicator;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicatorFactory;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.util.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.BiWrapper;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.Service;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceStarter {
    private final StartupIndicatorFactory startupIndicatorFactory;
    private final ServicePinger servicePinger;
    private final PropertyDao propertyDao;

    /**
     * Starts the provided services.
     *
     * @param services            to start
     * @param startupCountLimit   how many services can be started in parallel
     * @param additionalArguments additional arguments to pass to ALL of the services
     */
    public void startServices(List<Service> services, int startupCountLimit, Map<String, String> additionalArguments) {
        List<String> serviceNames = services.stream()
            .map(Service::getName)
            .toList();
        StartupIndicator startupIndicator = startupIndicatorFactory.createFromServiceNames(serviceNames);

        List<BiWrapper<Integer, List<Service>>> groupedServices = services.stream()
            .collect(Collectors.groupingBy(Service::getGroup))
            .entrySet()
            .stream()
            .map(e -> new BiWrapper<>(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingInt(BiWrapper::getEntity1))
            .toList();

        groupedServices.forEach(group -> startServiceGroup(startupIndicator, group.getEntity1(), group.getEntity2(), startupCountLimit, additionalArguments));

        startupIndicator.scheduleShutdown();
    }

    private void startServiceGroup(StartupIndicator startupIndicator, Integer group, List<Service> groupMembers, int startupCountLimit, Map<String, String> additionalArguments) {
        log.info("");
        log.info("Starting up serviceGroup {}", group);

        ExecutorServiceBean executorServiceBean = new ExecutorServiceBean(Executors.newFixedThreadPool(startupCountLimit));

        try {
            Map<Service, FutureWrapper<Void>> executionResults = groupMembers.stream()
                .collect(Collectors.toMap(Function.identity(), service -> executorServiceBean.execute(() -> startService(service, startupIndicator, additionalArguments))));

            executionResults.forEach((service, voidFutureWrapper) -> {
                try {
                    voidFutureWrapper.get()
                        .getOrThrow();
                } catch (Exception e) {
                    log.error("Failed starting service {}", service.getName(), e);
                }
            });
        } finally {
            executorServiceBean.stop();
        }
    }

    public void startService(Service service, StartupIndicator startupIndicator) {
        startService(service, startupIndicator, Map.of());
    }

    @SneakyThrows
    private void startService(Service service, StartupIndicator startupIndicator, Map<String, String> additionalArguments) {
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
        command.addAll(mapArguments(getArguments(service.getProperties()), additionalArguments));
        command.addAll(List.of(
            "-jar",
            service.getLocation()
        ));

        new ProcessBuilder(command)
            .start();

        startupIndicator.startupInitiated(service.getName());

        Integer port = Optional.ofNullable(service.getHealthCheckPort())
            .orElse(service.getPort());

        servicePinger.pingLocal(port)
            .ifPresent(e -> {
                throw new RuntimeException("Failed starting service " + service.getName(), e);
            });

        stopwatch.stop();
        startupIndicator.startupCompleted(service.getName());
        log.info("{} successfully started in {}s.", service.getName(), (stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d));
    }

    private Map<String, String> getArguments(List<PropertyName> properties) {
        return properties.stream()
            .flatMap(
                propertyName -> propertyDao.getEnvironmentSpecificProperties(propertyName)
                    .getForEnvironmentOrDefault(Environment.LOCAL)
                    .entrySet()
                    .stream()
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Maps the service arguments and additional arguments to a collection of strings in the format "-Dkey=value".
     */
    private Collection<String> mapArguments(Map<String, String> serviceArguments, Map<String, String> additionalArguments) {
        Map<String, String> arguments = new HashMap<>(serviceArguments);
        arguments.putAll(additionalArguments);

        return arguments.entrySet()
            .stream()
            .map(entry -> "-D%s=%s".formatted(entry.getKey(), entry.getValue()))
            .toList();
    }
}
