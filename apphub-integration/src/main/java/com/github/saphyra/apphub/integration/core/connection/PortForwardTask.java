package com.github.saphyra.apphub.integration.core.connection;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.framework.Constants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PortForwardTask {
    @SneakyThrows
    public static void portForwardService(Integer localPort) {
        log.debug("Forwarding port {} of service {} in namespace {} to local port {}", TestConfiguration.SERVER_PORT, Constants.SERVICE_NAME_MAIN_GATEWAY, TestConfiguration.NAMESPACE_NAME, localPort);

        new ProcessBuilder("kubectl", "port-forward", "deployment/%s".formatted(Constants.SERVICE_NAME_MAIN_GATEWAY), "%s:%s".formatted(localPort, TestConfiguration.SERVER_PORT), "-n", TestConfiguration.NAMESPACE_NAME)
            .start();
    }

    @SneakyThrows
    public static void portForwardDatabase(Integer localPort) {
        log.debug("Forwarding port {} of service {} in namespace {} to local port {}", TestConfiguration.DATABASE_PORT, Constants.SERVICE_NAME_POSTGRES, TestConfiguration.NAMESPACE_NAME, localPort);

        new ProcessBuilder("kubectl", "port-forward", "deployment/%s".formatted(Constants.SERVICE_NAME_POSTGRES), "%s:%s".formatted(localPort, TestConfiguration.DATABASE_PORT), "-n", TestConfiguration.NAMESPACE_NAME)
            .start();
    }
}
