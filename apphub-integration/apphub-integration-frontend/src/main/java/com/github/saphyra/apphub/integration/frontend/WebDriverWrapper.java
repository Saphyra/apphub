package com.github.saphyra.apphub.integration.frontend;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class WebDriverWrapper {
    private final UUID id = UUID.randomUUID();
    private WebDriver driver;
    private volatile boolean locked;
}
