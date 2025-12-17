package com.github.saphyra.apphub.integration.structure.view.calendar;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

@RequiredArgsConstructor
public class CalendarOccurrence {
    private static final String STATUS_PREFIX = "calendar-occurrence-";

    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(By.className("calendar-occurrence-title"))
            .getText();
    }

    public void open(WebDriver driver) {
        webElement.click();

        WebElementUtils.waitForSpinnerToDisappear(driver);
    }

    public OccurrenceStatus getStatus() {
        return WebElementUtils.getClasses(webElement)
            .stream()
            .filter(c -> c.startsWith(STATUS_PREFIX))
            .map(c -> c.replace(STATUS_PREFIX, ""))
            .map(OccurrenceStatus::parse)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Status cannot be determined."));
    }
}
