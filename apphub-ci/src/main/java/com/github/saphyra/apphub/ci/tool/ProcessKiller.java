package com.github.saphyra.apphub.ci.tool;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessKiller {
    public void killByPort(int port) {
        if (port == 5432) {
            throw new IllegalArgumentException("PostgreSQL port 5432 must not be terminated.");
        }

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
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.warn("Failed stopping process listening on port {}", port, e);
        }
    }

    @SneakyThrows
    private static void killProcessByPID(int port, String pid) throws IOException {
        Runtime.getRuntime().exec("taskkill /F /PID " + pid)
            .waitFor();
        log.debug("Process listening on port {} is killed.", port);
    }
}
