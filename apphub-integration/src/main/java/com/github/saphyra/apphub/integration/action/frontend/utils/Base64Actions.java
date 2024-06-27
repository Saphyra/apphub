package com.github.saphyra.apphub.integration.action.frontend.utils;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Base64Actions {
    public static void fillInput(WebDriver driver, String input) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("base64-input")), input);
    }

    public static void encode(WebDriver driver) {
        driver.findElement(By.id("base64-encode-button"))
            .click();
    }

    public static String getOutput(WebDriver driver) {
        return driver.findElement(By.id("base64-output"))
            .getAttribute("value");
    }

    public static void decode(WebDriver driver) {
        driver.findElement(By.id("base64-decode-button"))
            .click();
    }
}
