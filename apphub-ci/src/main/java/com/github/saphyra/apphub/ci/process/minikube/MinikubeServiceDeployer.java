package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeServiceDeployer {
    private final Services services;
    private final MinikubePodStartupWaiter minikubePodStartupWaiter;

    public void deploy(String namespaceName, String serviceDir, List<String> servicesToStart) {
        services.getServices()
            .stream()
            .filter(service -> servicesToStart.contains(service.getName()))
            .collect(Collectors.groupingBy(Service::getGroup))
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> deploy(namespaceName, serviceDir, entry.getKey(), entry.getValue()));
    }

    public void deploy(String namespaceName, String serviceDir) {
        Stream.concat(services.getServices().stream(), Stream.of(Services.FRONTEND))
            .collect(Collectors.groupingBy(Service::getGroup))
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> deploy(namespaceName, serviceDir, entry.getKey(), entry.getValue()));
    }

    private void deploy(String namespaceName, String serviceDir, Integer group, List<Service> serviceList) {
        log.info("");
        log.info("Starting up serviceGroup {}", group);

        serviceList.forEach(service -> deploy(namespaceName, serviceDir, service));

        minikubePodStartupWaiter.waitForPods(namespaceName);
    }

    @SneakyThrows
    private void deploy(String namespaceName, String serviceDir, Service service) {
        log.info("");

        new ProcessBuilder("kubectl", "-n", namespaceName, "delete", "deployment", service.getName())
            .inheritIO()
            .start()
            .waitFor();
        new ProcessBuilder("kubectl", "-n", namespaceName, "delete", "service", service.getName())
            .inheritIO()
            .start()
            .waitFor();

        new ProcessBuilder("kubectl", "apply", "-n", namespaceName, "-f", "infra/service/%s/%s.yml".formatted(serviceDir, service.getName()))
            .inheritIO()
            .start()
            .waitFor();
    }
}
