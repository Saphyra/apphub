package com.github.saphyra.apphub.ci.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
public class ProcessKiller {
    public void killByPort(int port) {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c netstat -ano | findstr :" + port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("LISTENING")) {
                    String[] parts = line.trim().split("\\s+");
                    String pid = parts[parts.length - 1];
                    killProcessByPID(port, pid);
                }
            }
        } catch (IOException e) {
            log.info("Failed stopping process listening on port {}", port, e);
        }
    }

    private static void killProcessByPID(int port, String pid) throws IOException {
        Runtime.getRuntime().exec("taskkill /F /PID " + pid);
        log.debug("Process listening on port {} is killed.", port);
    }
}
