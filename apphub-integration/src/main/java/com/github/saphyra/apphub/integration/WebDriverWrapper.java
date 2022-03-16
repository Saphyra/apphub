package com.github.saphyra.apphub.integration;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.UUID;
import java.util.concurrent.Future;

@Data
@RequiredArgsConstructor
public class WebDriverWrapper {
    private final UUID id = UUID.randomUUID();
    private final Future<WebDriver> driver;

    public WebDriver getDriver() {
        try {
            return driver.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed extracting WebDriver", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WebDriverWrapper) {
            WebDriverWrapper wrapper = (WebDriverWrapper) o;
            return id.equals(wrapper.getId());
        }

        return false;
    }
}