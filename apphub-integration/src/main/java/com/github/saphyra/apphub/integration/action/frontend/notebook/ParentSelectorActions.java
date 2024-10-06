package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.SleepUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

public class ParentSelectorActions {
    public static void selectParent(WebDriver driver, String parentTitle) {
        getAvailableParents(driver)
            .filter(webElement -> webElement.getText().equals(parentTitle))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No parent available with title " + parentTitle))
            .click();

        SleepUtil.sleep(1000);
    }

    public static Stream<WebElement> getAvailableParents(WebDriver driver) {
        return driver.findElements(By.cssSelector(".notebook-parent-selector-children .notebook-parent-selector-child"))
            .stream();
    }

    public static void up(WebDriver driver) {
        driver.findElement(By.className("notebook-parent-selector-up-button"))
            .click();
    }

    public static String getParent(WebDriver driver) {
        return driver.findElement(By.id("notebook-parent-selector-selected-parent-title"))
            .getText();
    }
}
