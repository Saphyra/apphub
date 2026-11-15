package com.github.saphyra.apphub.ci.tool.service;

import com.github.saphyra.apphub.ci.dao.property.PropertyDao;
import com.github.saphyra.apphub.ci.dao.property.PropertyName;
import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicator;
import com.github.saphyra.apphub.ci.ui.progress.ProgressIndicatorFactory;
import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.util.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.BiWrapper;
import com.github.saphyra.apphub.ci.value.Constants;
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
    private final ProgressIndicatorFactory progressIndicatorFactory;
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
        ProgressIndicator progressIndicator = progressIndicatorFactory.createFromLabels(Constants.PANEL_TITLE_STARTUP_INDICATOR, serviceNames);

        List<BiWrapper<Integer, List<Service>>> groupedServices = services.stream()
            .collect(Collectors.groupingBy(Service::getGroup))
            .entrySet()
            .stream()
            .map(e -> new BiWrapper<>(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingInt(BiWrapper::getEntity1))
            .toList();

        groupedServices.forEach(group -> startServiceGroup(progressIndicator, group.getEntity1(), group.getEntity2(), startupCountLimit, additionalArguments));

        progressIndicator.scheduleShutdown();
    }

    private void startServiceGroup(ProgressIndicator progressIndicator, Integer group, List<Service> groupMembers, int startupCountLimit, Map<String, String> additionalArguments) {
        log.info("");
        log.info("Starting up serviceGroup {}", group);

        ExecutorServiceBean executorServiceBean = new ExecutorServiceBean(Executors.newFixedThreadPool(startupCountLimit));

        try {
            Map<Service, FutureWrapper<Void>> executionResults = groupMembers.stream()
                .collect(Collectors.toMap(Function.identity(), service -> executorServiceBean.execute(() -> startService(service, progressIndicator, additionalArguments))));

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

    public void startService(Service service, ProgressIndicator progressIndicator) {
        startService(service, progressIndicator, Map.of());
    }

    @SneakyThrows
    private void startService(Service service, ProgressIndicator progressIndicator, Map<String, String> additionalArguments) {
        try {
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

            progressIndicator.started(service.getName());

            Integer port = Optional.ofNullable(service.getHealthCheckPort())
                .orElse(service.getPort());

            servicePinger.pingLocal(port)
                .ifPresent(e -> {
                    throw new RuntimeException("Failed starting service " + service.getName(), e);
                });

            stopwatch.stop();
            progressIndicator.completed(service.getName());
            log.info("{} successfully started in {}s.", service.getName(), (stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d));
        } catch (Exception e) {
            progressIndicator.failed(service.getName());
            throw e;
        }
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
