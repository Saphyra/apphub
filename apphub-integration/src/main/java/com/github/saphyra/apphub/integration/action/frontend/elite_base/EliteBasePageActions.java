package com.github.saphyra.apphub.integration.action.frontend.elite_base;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.elite_base.EliteBasePageCategory;
import com.github.saphyra.apphub.integration.structure.view.elite_base.EliteBaseSubPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EliteBasePageActions {
    public static void openSubpage(WebDriver driver, EliteBaseSubPage subPage) {
        openCategories(driver, subPage.getParents());

        driver.findElement(By.id(subPage.getId()))
            .click();
    }

    private static void openCategories(WebDriver driver, EliteBasePageCategory[] parents) {
        for (EliteBasePageCategory parent : parents) {
            WebElement categoryElement = driver.findElement(By.id(parent.getCategoryId()));

            WebElementUtils.getIfPresent(() -> categoryElement.findElement(By.cssSelector(".elite-base-navigation-toggle-button.closed")))
                .ifPresent(WebElement::click);
        }
    }
}
