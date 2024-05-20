package com.github.saphyra.apphub.ci.process;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.PlatformProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RunTestsTask {
    private final PropertyDao propertyDao;
    private final PlatformProperties testProperties;

    public void localRunTests(String testGroups) {
        runTests(
            testGroups,
            propertyDao.getLocalRunTestsThreadCount(),
            testProperties.getLocalServerPort(),
            testProperties.getLocalDatabasePort(),
            testProperties.getLocalDatabaseName()
        );
    }

    public void remoteRunTests(String testGroups) {
        log.info("Running remote tests. Enabled test groups: {}", testGroups.isEmpty() ? "All" : testGroups);

        runTests(
            testGroups,
            propertyDao.getRemoteRunTestsThreadCount(),
            testProperties.getMinikubeTestServerPort(),
            testProperties.getMinikubeTestDatabasePort(),
            testProperties.getMinikubeDatabaseName()
        );
    }

    private void runTests(String testGroups, Integer threadCount, Integer serverPort, Integer databasePort, String databaseName) {
        List<String> command = List.of(
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
            "-DenabledGroups=%s".formatted(testGroups),
            "\"",
            "clean",
            "test"
        );

        String[] array = new String[command.size()];

        try {
            Process process = new ProcessBuilder(command.toArray(array))
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
