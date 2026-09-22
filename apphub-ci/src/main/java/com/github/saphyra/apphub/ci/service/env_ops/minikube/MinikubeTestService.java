package com.github.saphyra.apphub.ci.service.env_ops.minikube;

import com.github.saphyra.apphub.ci.service.env_ops.IntegrationServerStarter;
import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodStartupWaiter;
import com.github.saphyra.apphub.ci.tool.kubernetes.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.tool.test.TestRunner;
import com.github.saphyra.apphub.ci.tool.test.TestRunner.TestConfiguration;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinikubeTestService {
    private final NamespaceNameProvider namespaceNameProvider;
    private final KubernetesPodStartupWaiter kubernetesPodStartupWaiter;
    private final IntegrationServerStarter integrationServerStarter;
    private final TestRunner testRunner;
    private final PlatformProperties platformProperties;

    public void runTests(String testFilter, int threadCount, int preCreatedDriverCount, int retryCount) {
        String namespace = namespaceNameProvider.getNamespaceName();
        kubernetesPodStartupWaiter.waitForPods(namespace, 5);

        integrationServerStarter.start();

        testRunner.runTests(
            TestConfiguration.builder()
                .environment(Environment.MINIKUBE)
                .testFilter(testFilter)
                .threadCount(threadCount)
                .serverPort(platformProperties.getMinikubeMainGatewayPort())
                .databasePort(platformProperties.getMinikubeTestDatabasePort())
                .databaseName(platformProperties.getMinikubeDatabaseName())
                .disabledGroups("")
                .preCreateDrivers(preCreatedDriverCount)
                .serverConnectionCacheEnabled(true)
                .databaseConnectionCacheEnabled(true)
                .namespace(namespace)
                .retryCount(retryCount)
                .dynamoDbHost("localhost:" + platformProperties.getMinikubeDynamoDbPort())
                .build()
        );
    }
}
