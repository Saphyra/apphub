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
    private final PlatformProperties platformProperties;
    private final KillChromeDriverTask killChromeDriverTask;

    public void localRunTests(String testGroups) {
        try {
            runTests(
                testGroups,
                propertyDao.getLocalRunTestsThreadCount(),
                platformProperties.getLocalServerPort(),
                platformProperties.getLocalDatabasePort(),
                platformProperties.getLocalDatabaseName(),
                "",
                testGroups.length() > 0 ? 0 : propertyDao.getLocalRunPreCreateDriverCount()
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
                testGroups.length() > 0 ? 0 : propertyDao.getRemoteRunPreCreateDriverCount()
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
                propertyDao.getRemoteRunPreCreateDriverCount()
            );
        } finally {
            killChromeDriverTask.run();
        }
    }

    private void runTests(String enabledGroups, Integer threadCount, Integer serverPort, Integer databasePort, String databaseName, String disabledGroups, Integer preCreateDrivers) {
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
            "-DenabledGroups=%s".formatted(enabledGroups),
            "-DdisabledGroups=%s".formatted(disabledGroups),
            "-DpreCreateWebDrivers=%s".formatted(preCreateDrivers),
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
