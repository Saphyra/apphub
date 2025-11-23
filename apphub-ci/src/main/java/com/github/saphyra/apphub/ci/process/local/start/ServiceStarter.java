package com.github.saphyra.apphub.ci.process.local.start;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.process.local.LocalStartTask;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicator;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicatorFactory;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.utils.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.utils.concurrent.FutureWrapper;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ServiceStarter {
    private final Services services;
    private final ServicePinger servicePinger;
    private final PropertyDao propertyDao;
    private final StartupIndicatorFactory startupIndicatorFactory;

    public void startServices(List<String> servicesToStart) {
        StartupIndicator startupIndicator = startupIndicatorFactory.createFromServiceNames(servicesToStart);

        servicesToStart.stream()
            .map(serviceName -> services.findByName(serviceName).orElseThrow(() -> new RuntimeException("Service not found with name " + serviceName)))
            .collect(Collectors.groupingBy(Service::getGroup))
            .forEach((group, groupMembers) -> startServices(startupIndicator, group, groupMembers));
    }

    void startServices() {
        List<String> disabledServices = propertyDao.getDisabledServices();

        log.info("The following services are disabled, they won't start: {}", disabledServices);

        List<Service> servicesToStart = services.getServices()
            .stream()
            .filter(service -> !disabledServices.contains(service.getName()))
            .toList();

        Stopwatch stopwatch = Stopwatch.createStarted();

        StartupIndicator startupIndicator = startupIndicatorFactory.createFromServiceList(servicesToStart);

        servicesToStart.stream()
            .collect(Collectors.groupingBy(Service::getGroup))
            .entrySet()
            .stream()
            .sorted(Comparator.comparingInt(Map.Entry::getKey))
            .forEach(entry -> startServices(startupIndicator, entry.getKey(), entry.getValue()));

        stopwatch.stop();
        startupIndicator.scheduleShutdown();
        log.info("\nServices started up in {}s", stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d);
    }

    private void startServices(StartupIndicator startupIndicator, Integer group, List<Service> groupMembers) {
        log.info("");
        log.info("Starting up serviceGroup {}", group);

        ExecutorServiceBean executorServiceBean = new ExecutorServiceBean(Executors.newFixedThreadPool(propertyDao.getLocalStartupCountLimit()));

        try {
            Map<Service, FutureWrapper<Void>> executionResults = groupMembers.stream()
                .collect(Collectors.toMap(Function.identity(), service -> executorServiceBean.execute(LocalStartTask.builder()
                    .servicePinger(servicePinger)
                    .service(service)
                    .startupIndicator(startupIndicator)
                    .build()
                )));

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
}
