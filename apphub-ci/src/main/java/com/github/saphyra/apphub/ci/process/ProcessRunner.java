package com.github.saphyra.apphub.ci.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
public class ProcessRunner {
    public void runProcess(String command) {
        try {
            log.info("Running command {}", command);

            Process process = Runtime.getRuntime().exec(String.format("cmd /c %s", command));

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Command {} finished successfully.", command);
            } else {
                log.error("Command {} failed with exitCode {}", command, exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Command {} failed.", command, e);
        }
    }
}
