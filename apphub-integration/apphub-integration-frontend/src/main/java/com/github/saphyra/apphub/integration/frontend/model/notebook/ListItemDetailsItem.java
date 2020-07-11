package com.github.saphyra.apphub.integration.frontend.model.notebook;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@AllArgsConstructor
public class ListItemDetailsItem {
    private static final By TITLE = By.cssSelector(":scope > span");
    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(TITLE).getText();
    }
}
