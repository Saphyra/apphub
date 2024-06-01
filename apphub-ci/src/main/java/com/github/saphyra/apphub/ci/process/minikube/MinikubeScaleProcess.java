package com.github.saphyra.apphub.ci.process.minikube;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeScaleProcess {
    @SneakyThrows
    public void scale(String namespace, int replicaCount) {
        log.info("Setting replicaCount to {} on namespace {}", replicaCount, namespace);

        String command = "kubectl -n %s scale deployments --replicas=%s --all".formatted(namespace, replicaCount);

        new ProcessBuilder(command.split(" "))
            .inheritIO()
            .start()
            .waitFor();
    }
}
