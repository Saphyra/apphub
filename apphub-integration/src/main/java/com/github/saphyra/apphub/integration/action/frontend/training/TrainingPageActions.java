package com.github.saphyra.apphub.integration.action.frontend.training;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TrainingPageActions {
    public static List<WebElement> getMenuItems(WebDriver driver) {
        return TrainingPage.menuItems(driver);
    }

    public static WebElement getChapterHead(WebDriver driver) {
        return TrainingPage.chapterHead(driver);
    }

    public static boolean doesNextButtonExist(WebDriver driver) {
        return WebElementUtils.getIfPresent(() -> TrainingPage.nextButton(driver))
            .isPresent();
    }

    public static void nextPage(WebDriver driver) {
        TrainingPage.nextButton(driver)
            .click();

        waitForPageLoad(driver);
    }

    public static boolean doesPreviousButtonExist(WebDriver driver) {
        return WebElementUtils.getIfPresent(() -> TrainingPage.previousButton(driver))
            .isPresent();
    }

    public static void previousPage(WebDriver driver) {
        TrainingPage.previousButton(driver)
            .click();

        waitForPageLoad(driver);
    }


    public static void openMenuItem(WebDriver driver, int index) {
        TrainingPageActions.getMenuItems(driver)
            .get(index)
            .click();

        waitForPageLoad(driver);
    }

    private static void waitForPageLoad(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(() -> TrainingPage.homeButton(driver)).isPresent())
            .assertTrue("Home button not loaded.");
    }
}
