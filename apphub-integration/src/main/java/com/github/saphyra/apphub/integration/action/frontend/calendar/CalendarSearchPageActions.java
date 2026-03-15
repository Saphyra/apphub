package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CalendarSearchPageActions {
    public static void setSearchText(WebDriver driver, String searchText) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-search-input")), searchText);
    }

    public static List<WebElement> getSearchResult(WebDriver driver) {
        return driver.findElements(By.cssSelector("#calendar-search-event-list .calendar-search-event"));
    }
}
