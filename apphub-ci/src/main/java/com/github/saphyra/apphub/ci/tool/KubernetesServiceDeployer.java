package com.github.saphyra.apphub.ci.tool;

import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class KubernetesServiceDeployer {
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;

    public void deploy(String namespaceName, String serviceDir, List<Service> servicesToStart, int waitCount, int batchSize) {
        servicesToStart.stream()
            .collect(Collectors.groupingBy(Service::getGroup))
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> deploy(namespaceName, serviceDir, entry.getKey(), entry.getValue(), waitCount, batchSize));
    }

    private void deploy(String namespaceName, String serviceDir, Integer group, List<Service> serviceList, int waitCount, int batchSize) {
        List<List<Service>> batches = ListUtils.partition(serviceList, batchSize);

        for (int batch = 0; batch < batches.size(); batch++) {
            log.info("");
            log.info("Starting up serviceGroup {}, batch {}/{}", group, batch + 1, batches.size());

            batches.get(batch).forEach(service -> deploy(namespaceName, serviceDir, service));

            kubernetesPodStartupWaiter.waitForPods(namespaceName, waitCount);
        }
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
