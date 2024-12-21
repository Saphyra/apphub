package com.github.saphyra.apphub.ci.process;

import com.github.saphyra.apphub.ci.process.local.LocalStartTask;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegrationServerStarter {
    private final PlatformProperties testProperties;
    private final ServicePinger servicePinger;

    public void start() {
        log.info("Starting integration server...");
        Service integrationServer = testProperties.getIntegrationServer();
        if (servicePinger.singlePingLocal(integrationServer.getPort()).isPresent()) {
            LocalStartTask.builder()
                .servicePinger(servicePinger)
                .service(integrationServer)
                .build()
                .run();
        } else {
            log.info("Integration server is already running.");
        }
    }
}
