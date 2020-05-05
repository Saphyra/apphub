package com.github.saphyra.apphub.integration.frontend.framework;

import org.openqa.selenium.WebElement;

public class WebElementUtils {

    public static void clearAndFill(WebElement webElement, String text){
        webElement.clear();
        webElement.sendKeys(text);
    }
}
