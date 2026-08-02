package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CalendarSharePageActions {
    public static void selectUser(WebDriver driver, String credential) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-share-with-search-input")), credential);

        driver.findElement(By.id("calendar-share-with-search-button"))
            .click();

        AwaitilityWrapper.getListWithWait(() -> driver.findElements(By.cssSelector("#calendar-share-with-search-result .calendar-share-with-search-result-item")), l -> !l.isEmpty())
            .getFirst()
            .click();
    }

    public static void selectAllGrants(WebDriver driver) {
        driver.findElement(By.cssSelector("#calendar-share-with-selected-user-grants .multi-select-select-all"))
            .click();
    }

    public static void share(WebDriver driver) {
        int originalSize = getSharedWith(driver).size();

        driver.findElement(By.id("calendar-share-with-selected-user-share-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> getSharedWith(driver).size() > originalSize)
            .assertTrue("Item is not shared with user.");
    }

    private static List<WebElement> getSharedWith(WebDriver driver) {
        return driver.findElements(By.cssSelector("#calendar-shared-with-list .shared-with-user"));
    }

    public static void back(WebDriver driver) {
        driver.findElement(By.id("calendar-labels-back-button"))
            .click();
    }

    public static void toggleGrant(WebDriver driver, Grant grant) {
        driver.findElement(By.id("calendar-share-with-selected-user-grants"))
            .findElement(By.cssSelector("input[value='%s']".formatted(grant.name())))
            .click();
    }
}
