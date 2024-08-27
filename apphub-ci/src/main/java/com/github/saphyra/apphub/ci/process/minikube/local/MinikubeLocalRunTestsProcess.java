package com.github.saphyra.apphub.ci.process.minikube.local;

import com.github.saphyra.apphub.ci.process.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.process.RunTestsTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeLocalRunTestsProcess {
    private final IntegrationServerStarter integrationServerStarter;
    private final RunTestsTask runTestsTask;

    public void runTests() {
        runTests("");
    }

    public void runTests(String testGroups) {
        try {
            log.info("");
            integrationServerStarter.start();

            runTestsTask.remoteRunTests(testGroups);

        } catch (Exception e) {
            log.error("Test run failed.", e);
        }
    }
}
