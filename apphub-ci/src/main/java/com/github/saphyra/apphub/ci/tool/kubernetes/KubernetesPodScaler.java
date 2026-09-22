package com.github.saphyra.apphub.ci.tool.kubernetes;

import com.github.saphyra.apphub.ci.util.concurrent.ExecutorServiceBean;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KubernetesPodScaler {
    private final ExecutorServiceBean executorServiceBean;

    @SneakyThrows
    public void scaleAll(String namespace, int replicaCount) {
        log.info("Setting replicaCount to {} on namespace {}", replicaCount, namespace);

        String command = "kubectl -n %s scale deployments --replicas=%s --all".formatted(namespace, replicaCount);

        new ProcessBuilder(command.split(" "))
            .inheritIO()
            .start()
            .waitFor();
    }

    public void scale(List<Service> services, String namespace, int replicaCount) {
        executorServiceBean.processCollectionWithWait(services, service -> scale(service, namespace, replicaCount));
    }

    @SneakyThrows
    private int scale(Service service, String namespace, int replicaCount) {
        String command = "kubectl -n %s scale deployment %s --replicas=%s".formatted(namespace, service.getName(), replicaCount);

        return new ProcessBuilder(command.split(" "))
            .inheritIO()
            .start()
            .waitFor();
    }
}
