package com.github.saphyra.apphub.ci.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ChromeDriverKiller {
    public void kill() {
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
