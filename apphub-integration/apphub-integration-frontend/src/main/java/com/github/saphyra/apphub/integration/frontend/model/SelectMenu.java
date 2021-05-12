package com.github.saphyra.apphub.integration.frontend.model;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class SelectMenu {
    private final WebElement webElement;

    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    public void selectOption(String value) {
        webElement.click();
        webElement.findElement(By.cssSelector(String.format(":scope option[value=\"%s\"]", value)))
            .click();
    }

    public String getValue() {
        return webElement.getAttribute("value");
    }
}
