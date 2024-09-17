package com.github.saphyra.apphub.integration.action.frontend.error;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ErrorPageActions {
    public static String getErrorCode(WebDriver driver) {
        return driver.findElement(By.id("error-code"))
            .getText();
    }

    public static String getErrorMessage(WebDriver driver) {
        return driver.findElement(By.id("error-message"))
            .getText();
    }
}
