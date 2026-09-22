package com.github.saphyra.apphub.ci.service.env_ops.preprod;

import com.github.saphyra.apphub.ci.service.env_ops.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodStartupWaiter;
import com.github.saphyra.apphub.ci.tool.test.TestRunner;
import com.github.saphyra.apphub.ci.tool.test.TestRunner.TestConfiguration;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreprodTestService {
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;
    private final IntegrationServerStarter integrationServerStarter;
    private final TestRunner testRunner;
    private final PlatformProperties platformProperties;

    public void runTests(String testFilter, int threadCount, int preCreatedDriverCount, int retryCount) {
        kubernetesPodStartupWaiter.waitForPods(Constants.NAMESPACE_NAME_PREPROD, 5);

        integrationServerStarter.start();

        testRunner.runTests(
            TestConfiguration.builder()
                .environment(Environment.PREPROD)
                .testFilter(testFilter)
                .threadCount(threadCount)
                .serverPort(platformProperties.getMinikubeMainGatewayPort())
                .databasePort(platformProperties.getLocalDatabasePort())
                .databaseName(platformProperties.getPreprodDatabaseName())
                .disabledGroups("")
                .preCreateDrivers(preCreatedDriverCount)
                .serverConnectionCacheEnabled(true)
                .databaseConnectionCacheEnabled(false)
                .namespace(Constants.NAMESPACE_NAME_PREPROD)
                .retryCount(retryCount)
                .dynamoDbHost("0")
                .build()
        );
    }
}
