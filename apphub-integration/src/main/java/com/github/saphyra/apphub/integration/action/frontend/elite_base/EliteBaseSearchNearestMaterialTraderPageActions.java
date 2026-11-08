package com.github.saphyra.apphub.integration.action.frontend.elite_base;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EliteBaseSearchNearestMaterialTraderPageActions {
    public static void setReferenceStarSystem(WebDriver driver, String starName) {
        By selector = By.id("elite-base-sn-material-trader-reference-system");

        WebElementUtils.clearAndFill(driver.findElement(selector), starName);

        AwaitilityWrapper.create(30, 1)
            .until(() -> WebElementUtils.getClasses(driver.findElement(selector)).contains("locked"))
            .assertTrue("Reference system is not found: " + starName);
    }

    public static void search(WebDriver driver) {
        driver.findElement(By.id("elite-base-sn-material-trader-search-button"))
            .click();
    }

    public static List<WebElement> getResult(WebDriver driver) {
        return driver.findElements(By.cssSelector("#elite-base-sn-material-trader-search-result .elite-base-sn-material-trader-result-row"));
    }
}
