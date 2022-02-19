package com.github.saphyra.apphub.integration.structure;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class ErrorMessageElement {
    private static final By ERROR_CODE = By.cssSelector("span:first-child");
    private static final By ERROR_MESSAGE = By.cssSelector("span:last-child");

    private final WebElement element;

    public String getErrorCode() {
        return element.findElement(ERROR_CODE).getText();
    }

    public String getErrorMessage() {
        return element.findElement(ERROR_MESSAGE).getText();
    }
}
