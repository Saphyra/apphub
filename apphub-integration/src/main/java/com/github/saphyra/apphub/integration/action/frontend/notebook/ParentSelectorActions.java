package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

public class ParentSelectorActions {
    public static void selectParent(WebDriver driver, String parentTitle) {
        AwaitilityWrapper.getSingleItemFromListWithWait(
                () -> getAvailableParents(driver).toList(),
                webElements -> webElements.stream().filter(element -> element.getText().equals(parentTitle)).findFirst()
            )
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.findElement(By.id("notebook-parent-selector-selected-parent-title")).getText().equals(parentTitle))
            .assertTrue("Parent is not selected");
    }

    public static Stream<WebElement> getAvailableParents(WebDriver driver) {
        return driver.findElements(By.cssSelector(".notebook-parent-selector-children .notebook-parent-selector-child"))
            .stream();
    }

    public static void up(WebDriver driver) {
        driver.findElement(By.className("notebook-parent-selector-up-button"))
            .click();

        WebElementUtils.waitForSpinnerToDisappear(driver);
    }

    public static String getParent(WebDriver driver) {
        return driver.findElement(By.id("notebook-parent-selector-selected-parent-title"))
            .getText();
    }
}
