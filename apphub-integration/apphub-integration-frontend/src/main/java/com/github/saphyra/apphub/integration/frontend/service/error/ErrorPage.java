package com.github.saphyra.apphub.integration.frontend.service.error;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class ErrorPage {
    private static final By ERROR_MESSAGE_ROOT = By.id("message-content");

    public static WebElement errorMessageRoot(WebDriver driver) {
        return driver.findElement(ERROR_MESSAGE_ROOT);
    }
}
