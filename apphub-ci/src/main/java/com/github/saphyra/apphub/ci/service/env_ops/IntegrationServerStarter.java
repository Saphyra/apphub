package com.github.saphyra.apphub.ci.service.env_ops;

import com.github.saphyra.apphub.ci.tool.ServicePinger;
import com.github.saphyra.apphub.ci.tool.ServiceStarter;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicator;
import com.github.saphyra.apphub.ci.ui.startup.StartupIndicatorFactory;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegrationServerStarter {
    private final PlatformProperties testProperties;
    private final ServicePinger servicePinger;
    private final StartupIndicatorFactory startupIndicatorFactory;
    private final ServiceStarter serviceStarter;

    @SneakyThrows
    public void start() {
        log.info("Starting integration server...");
        Service integrationServer = testProperties.getIntegrationServer();
        if (servicePinger.singlePingLocal(integrationServer.getPort()).isPresent()) {
            Process process = new ProcessBuilder("cmd", "/c", "cd", "apphub-integration-server", "&&", "mvn", "clean", "package")
                .inheritIO()
                .start();

            process.waitFor();

            StartupIndicator startupIndicator = startupIndicatorFactory.noOpIndicator();

            serviceStarter.startService(integrationServer, startupIndicator);
        } else {
            log.info("Integration server is already running.");
        }
    }
}
