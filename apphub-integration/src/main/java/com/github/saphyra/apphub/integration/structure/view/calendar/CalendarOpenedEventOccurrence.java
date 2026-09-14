package com.github.saphyra.apphub.integration.structure.view.calendar;

import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;

@RequiredArgsConstructor
public class CalendarOpenedEventOccurrence {
    private final WebElement webElement;

    public LocalDate getDate() {
        return LocalDate.parse(webElement.findElement(By.className("calendar-opened-event-occurrence-date")).getText());
    }

    public void open() {
        webElement.click();
    }

    public boolean isShared() {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.className("calendar-opened-event-occurrence-shared")))
            .map(webElement -> webElement.getAttribute("textContent"))
            .filter(Constants.SHARED_SUFFIX::equals)
            .isPresent();
    }
}
