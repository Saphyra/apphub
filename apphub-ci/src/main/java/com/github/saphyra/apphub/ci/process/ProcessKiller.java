package com.github.saphyra.apphub.ci.process;

import com.github.saphyra.apphub.ci.value.Services;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProcessKiller {
    private final Services services;

    public void killByServiceName(String serviceName) {
        log.info("Stopping service {}", serviceName);

        services.findByName(serviceName)
            .ifPresentOrElse(
                service -> killByPort(service.getPort()),
                () -> log.info("Service not found by name {}", serviceName)
            );
    }

    @SneakyThrows
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
            process.waitFor();
        } catch (IOException e) {
            log.info("Failed stopping process listening on port {}", port, e);
        }
    }

    @SneakyThrows
    private static void killProcessByPID(int port, String pid) throws IOException {
        Runtime.getRuntime().exec("taskkill /F /PID " + pid)
            .waitFor();
        log.debug("Process listening on port {} is killed.", port);
    }
}
