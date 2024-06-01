package com.github.saphyra.apphub.ci.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class KillChromeDriverTask {
    public void run() {
        try {
            Process process = new ProcessBuilder("taskkill", "/F", "/IM", "chromedriver.exe", "/T")
                .inheritIO()
                .start();

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Failed destroying chromedrivers", e);
        }
    }
}
