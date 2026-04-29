package com.github.saphyra.apphub.ci.process.local.start;

import com.github.saphyra.apphub.ci.process.ProcessKiller;
import com.github.saphyra.apphub.ci.process.local.stop.LocalStopProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalStartProcess {
    private final LocalStopProcess localStopProcess;
    private final LocalBuildTask localBuildTask;
    private final ServiceStarter serviceStarter;
    private final ProcessKiller processKiller;

    public void run() {
        localStopProcess.stopAllServices();
        if(!localBuildTask.buildServices()){
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        serviceStarter.startServices();
    }

    public void startServices(List<String> servicesToStart) {
        servicesToStart.forEach(processKiller::killByServiceName);

        if (!localBuildTask.buildServices(servicesToStart)) {
            log.error("Build failed. Startup sequence stopped.");
            return;
        }

        serviceStarter.startServices(servicesToStart);
    }
}
