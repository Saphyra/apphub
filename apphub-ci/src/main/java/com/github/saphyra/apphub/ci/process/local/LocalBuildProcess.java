package com.github.saphyra.apphub.ci.process.local;

import com.github.saphyra.apphub.ci.dao.PropertyDao;
import com.github.saphyra.apphub.ci.value.LocalRunMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalBuildProcess {
    private final PropertyDao propertyDao;

    /**
     * @return true, if maven build is successful, false otherwise.
     */
    public boolean run() {
        LocalRunMode localRunMode = propertyDao.getLocalRunMode();
        if (localRunMode == LocalRunMode.SKIP_BUILD) {
            return true;
        }

        List<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add("mvn");
        command.add("-T");
        command.add(String.valueOf(propertyDao.getThreadCount(localRunMode)));
        command.add("clean");
        command.add("package");
        if(localRunMode == LocalRunMode.SKIP_TESTS){
            command.add("-DskipTests");
        }

        String[] array = new String[command.size()];

        try {
            Process process = new ProcessBuilder(command.toArray(array))
                .inheritIO()
                .start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Build successful!");
                return true;
            } else {
                log.error("Build failed.");
                return false;
            }
        } catch (Exception e) {
            log.error("Build failed with exception", e);
            return false;
        }
    }
}
