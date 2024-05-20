package com.github.saphyra.apphub.ci.process.minikube;

import com.github.saphyra.apphub.ci.process.ProcessKiller;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortForwardTask {
    private final ProcessKiller processKiller;

    @SneakyThrows
    public void portForward(String namespaceName, String serviceName, Integer localPort, Integer servicePort) {
        log.info("Forwarding port {} of service {} in namespace {} to local port {}", servicePort, serviceName, namespaceName, localPort);
        processKiller.killByPort(localPort);

        new ProcessBuilder("kubectl", "port-forward", "deployment/%s".formatted(serviceName), "%s:%s".formatted(localPort, servicePort), "-n", namespaceName)
            .start();
    }
}
