package com.github.saphyra.apphub.ci.process;

import com.github.saphyra.apphub.ci.process.local.LocalStartTask;
import com.github.saphyra.apphub.ci.utils.ServicePinger;
import com.github.saphyra.apphub.ci.value.Service;
import com.github.saphyra.apphub.ci.value.TestProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegrationServerStarter {
    private final TestProperties testProperties;
    private final ServicePinger servicePinger;

    public void start() {
        Service integrationServer = testProperties.getIntegrationServer();
        if (servicePinger.singlePing(integrationServer.getPort()).isPresent()) {
            new LocalStartTask(servicePinger, integrationServer)
                .run();
        }else{
            log.info("Integration server is already running.");
        }
    }
}
