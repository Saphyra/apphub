package com.github.saphyra.apphub.ci.process;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.process.minikube.NamespaceNameProvider;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class RunTestsTask {
    private final PropertyDao propertyDao;
    private final PlatformProperties platformProperties;
    private final KillChromeDriverTask killChromeDriverTask;
    private final NamespaceNameProvider namespaceNameProvider;

    public void localRunTests(String testGroups) {
        try {
            runTests(
                testGroups,
                propertyDao.getLocalRunTestsThreadCount(),
                platformProperties.getLocalServerPort(),
                platformProperties.getLocalDatabasePort(),
                platformProperties.getLocalDatabaseName(),
                "",
                !testGroups.isEmpty() ? 0 : propertyDao.getLocalRunPreCreateDriverCount(),
                false,
                false,
                "",
                propertyDao.getLocalIntegrationRetryCount(),
                Environment.LOCAL,
                "localhost:" + platformProperties.getLocalDynamoDbPort()
            );
        } finally {
            killChromeDriverTask.run();
        }

    }

    public void remoteRunTests(String testGroups) {
        log.info("Running remote tests. Enabled test groups: {}", testGroups.isEmpty() ? "All" : testGroups);
        try {
            runTests(
                testGroups,
                propertyDao.getRemoteRunTestsThreadCount(),
                platformProperties.getMinikubeTestServerPort(),
                platformProperties.getMinikubeTestDatabasePort(),
                platformProperties.getMinikubeDatabaseName(),
                "",
                !testGroups.isEmpty() ? 0 : propertyDao.getRemoteRunPreCreateDriverCount(),
                true,
                true,
                namespaceNameProvider.getNamespaceName(),
                propertyDao.getRemoteIntegrationRetryCount(),
                Environment.MINIKUBE,
                "localhost:" + platformProperties.getMinikubeDynamoDbPort()
            );
        } finally {
            killChromeDriverTask.run();
        }
    }

    public void preprodRunTests(String enabledGroups) {
        log.info("Running preprod tests. Disabled test groups: {}", platformProperties.getProdDisabledTestGroups().isEmpty() ? "None" : platformProperties.getProdDisabledTestGroups());
        try {
            runTests(
                enabledGroups,
                propertyDao.getRemoteRunTestsThreadCount(),
                platformProperties.getMinikubeTestServerPort(),
                platformProperties.getLocalDatabasePort(),
                "apphub_preprod",
                String.join(",", platformProperties.getProdDisabledTestGroups()),
                enabledGroups.isEmpty() ? propertyDao.getRemoteRunPreCreateDriverCount() : 0,
                true,
                false,
                Constants.NAMESPACE_NAME_PREPROD,
                propertyDao.getRemoteIntegrationRetryCount(),
                Environment.PREPROD,
                "0"
            );
        } finally {
            killChromeDriverTask.run();
        }
    }

    public void productionRunTests() {
        log.info("Running production tests. Disabled test groups: {}", platformProperties.getProdDisabledTestGroups().isEmpty() ? "None" : platformProperties.getProdDisabledTestGroups());
        try {
            runTests(
                "",
                propertyDao.getRemoteRunTestsThreadCount(),
                platformProperties.getMinikubeTestServerPort(),
                platformProperties.getLocalDatabasePort(),
                platformProperties.getProdDatabaseName(),
                String.join(",", platformProperties.getProdDisabledTestGroups()),
                propertyDao.getRemoteRunPreCreateDriverCount(),
                true,
                false,
                Constants.NAMESPACE_NAME_PRODUCTION,
                propertyDao.getRemoteIntegrationRetryCount(),
                Environment.PRODUCTION,
                "0"
            );
        } finally {
            killChromeDriverTask.run();
        }
    }

    private void runTests(
        String enabledGroups,
        Integer threadCount,
        Integer serverPort,
        Integer databasePort,
        String databaseName,
        String disabledGroups,
        Integer preCreateDrivers,
        boolean serverConnectionCacheEnabled,
        boolean databaseConnectionCacheEnabled,
        String namespace,
        Integer retryCount,
        Environment environment,
        String dynamoDbHost
    ) {
        disabledGroups = String.join(",", disabledGroups, "community");

        Map<String, String> dynamoDbProperties = propertyDao.getEnvironmentSpecificProperties(PropertyName.DYNAMO_DB_CONFIGURATION)
            .getForEnvironmentOrDefault(environment);

        List<String> command = new ArrayList<>();
        command.addAll(
            List.of(
                "cmd",
                "/c",
                "cd",
                "apphub-integration",
                "&&",
                "mvn",
                "-DthreadCount=\"%s\"".formatted(threadCount),
                "-DargLine=\"",
                "-DthreadCount=%s".formatted(threadCount),
                "-DserverPort=%s".formatted(serverPort),
                "-DdatabasePort=%s".formatted(databasePort),
                "-Dheadless=true",
                "-DretryEnabled=true",
                "-DrestLoggingEnabled=false",
                "-DdatabaseName=%s".formatted(databaseName),
                "-DintegrationServerEnabled=true",
                "-DenabledGroups=%s".formatted(enabledGroups),
                "-DdisabledGroups=%s".formatted(disabledGroups),
                "-DpreCreateWebDrivers=%s".formatted(preCreateDrivers),
                "-DnamespaceName=%s".formatted(namespace),
                "-DserverConnectionCacheEnabled=%s".formatted(serverConnectionCacheEnabled),
                "-DdatabaseConnectionCacheEnabled=%s".formatted(databaseConnectionCacheEnabled),
                "-DbrowserStartupLimit=%s".formatted(propertyDao.getBrowserStartupLimit()),
                "-DmaxRetryCount=%s".formatted(retryCount),
                "-Denvironment=%s".formatted(environment.name().toLowerCase())
                )
        );

        if (!isBlank(dynamoDbHost)) {
            command.add("-DdynamoDbHost=%s".formatted(dynamoDbHost));
        }

        if (!dynamoDbProperties.isEmpty()) {
            command.add("-DdynamoDbAccessKeyId=%s".formatted(dynamoDbProperties.get(Constants.DYNAMO_DB_ACCESS_KEY_ID)));
            command.add("-DdynamoDbSecretKey=%s".formatted(dynamoDbProperties.get(Constants.DYNAMO_DB_SECRET_KEY)));
        }

        command.addAll(List.of(
            "\"",
            "clean",
            "test"
        ));

        try {
            Process process = new ProcessBuilder(command)
                .inheritIO()
                .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Tests passed!");
            } else {
                log.error("Tests failed.");
            }
        } catch (Exception e) {
            log.error("Tests failed with exception", e);
        }
    }
}
