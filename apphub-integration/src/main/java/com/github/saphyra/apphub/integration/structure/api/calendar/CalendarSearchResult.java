package com.github.saphyra.apphub.integration.structure.api.calendar;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

@RequiredArgsConstructor
public class CalendarSearchResult {
    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(By.cssSelector(":scope .search-result-title"))
            .getText();
    }

    public void openOccurrences() {
        if (isOccurrencesOpened()) {
            return;
        }

        webElement.findElement(By.cssSelector(":scope .search-result-toggle-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(this::isOccurrencesOpened)
            .assertTrue("Occurrences not opened.");
    }

    private boolean isOccurrencesOpened() {
        return webElement.findElement(By.cssSelector(":scope .search-result-occurrences"))
            .isDisplayed();
    }

    public List<WebElement> getOccurrences() {
        return webElement.findElements(By.cssSelector(":scope .search-result-occurrences .search-result-occurrence"));
    }
}
