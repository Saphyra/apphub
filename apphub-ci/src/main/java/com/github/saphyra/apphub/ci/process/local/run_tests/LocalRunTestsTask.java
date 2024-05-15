package com.github.saphyra.apphub.ci.process.local.run_tests;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.TestProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class LocalRunTestsTask {
    private final PropertyDao propertyDao;
    private final TestProperties testProperties;

    public void runTests() {
        Integer threadCount = propertyDao.getLocalRunTestsThreadCount();

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
            "-DserverPort=%s".formatted(testProperties.getLocalServerPort()),
            "-DdatabasePort=%s".formatted(testProperties.getLocalDatabasePort()),
            "-Dheadless=true",
            "-DretryEnabled=true",
            "-DrestLoggingEnabled=false",
            "-DdatabaseName=%s".formatted(testProperties.getLocalDatabaseName()),
            "-DintegrationServerEnabled=true",
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
