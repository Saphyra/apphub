package com.github.saphyra.apphub.ci.tool;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.dao.PropertyName;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Environment;
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

    public void runTests(
        Environment environment,
        String testFilter,
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
        String dynamoDbHost
    ) {
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
                "-DenabledGroups=%s".formatted(testFilter),
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
