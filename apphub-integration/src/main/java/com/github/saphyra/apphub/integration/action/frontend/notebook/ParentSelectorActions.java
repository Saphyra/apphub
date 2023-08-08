package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.SleepUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ParentSelectorActions {
    public static void selectParent(WebDriver driver, String parentTitle) {
        driver.findElements(By.cssSelector(".notebook-parent-selector-children .notebook-parent-selector-child"))
            .stream()
            .filter(webElement -> webElement.getText().equals(parentTitle))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No parent available with title " + parentTitle))
            .click();

        SleepUtil.sleep(1000);
    }

    public static void up(WebDriver driver) {
        driver.findElement(By.className("notebook-parent-selector-up-button"))
            .click();
    }
}
