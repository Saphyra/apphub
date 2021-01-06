package com.github.saphyra.apphub.integration.frontend.service.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class CommonPage {
    public static WebElement deletionConfirmationDialog(WebDriver driver, String id) {
        return driver.findElement(By.id(id));
    }
}
