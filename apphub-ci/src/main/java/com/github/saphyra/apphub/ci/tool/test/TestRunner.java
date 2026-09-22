package com.github.saphyra.apphub.ci.tool.test;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
import lombok.Builder;
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
public class TestRunner {
    private final PropertyDao propertyDao;
    private final ChromeDriverKiller chromeDriverKiller;

    public void runTests(TestConfiguration configuration) {
        Map<String, String> dynamoDbProperties = propertyDao.getEnvironmentSpecificProperties(PropertyName.DYNAMO_DB_CONFIGURATION)
            .getForEnvironmentOrDefault(configuration.environment);

        List<String> command = new ArrayList<>();
        command.addAll(
            List.of(
                "cmd",
                "/c",
                "cd",
                "apphub-integration",
                "&&",
                "mvn",
                "-DthreadCount=\"%s\"".formatted(configuration.threadCount),
                "-DargLine=\"",
                "-DthreadCount=%s".formatted(configuration.threadCount),
                "-DserverPort=%s".formatted(configuration.serverPort),
                "-DdatabasePort=%s".formatted(configuration.databasePort),
                "-Dheadless=true",
                "-DretryEnabled=true",
                "-DrestLoggingEnabled=false",
                "-DdatabaseName=%s".formatted(configuration.databaseName),
                "-DintegrationServerEnabled=true",
                "-DenabledGroups=%s".formatted(configuration.testFilter),
                "-DdisabledGroups=%s".formatted(configuration.disabledGroups),
                "-DpreCreateWebDrivers=%s".formatted(configuration.preCreateDrivers),
                "-DnamespaceName=%s".formatted(configuration.namespace),
                "-DserverConnectionCacheEnabled=%s".formatted(configuration.serverConnectionCacheEnabled),
                "-DdatabaseConnectionCacheEnabled=%s".formatted(configuration.databaseConnectionCacheEnabled),
                "-DbrowserStartupLimit=%s".formatted(propertyDao.getBrowserStartupLimit()),
                "-DmaxRetryCount=%s".formatted(configuration.retryCount),
                "-Denvironment=%s".formatted(configuration.environment.name().toLowerCase())
            )
        );

        if (!isBlank(configuration.dynamoDbHost)) {
            command.add("-DdynamoDbHost=%s".formatted(configuration.dynamoDbHost));
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
        } finally {
            chromeDriverKiller.kill();
        }
    }

    @Builder
    public static class TestConfiguration {
        private final Environment environment;
        private final String testFilter;
        private final Integer threadCount;
        private final Integer serverPort;
        private final Integer databasePort;
        private final String databaseName;
        private final String disabledGroups;
        private final Integer preCreateDrivers;
        private final boolean serverConnectionCacheEnabled;
        private final boolean databaseConnectionCacheEnabled;
        private final String namespace;
        private final Integer retryCount;
        private final String dynamoDbHost;
    }
}
