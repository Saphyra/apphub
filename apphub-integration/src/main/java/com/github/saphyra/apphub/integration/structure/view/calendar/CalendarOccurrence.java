package com.github.saphyra.apphub.integration.structure.view.calendar;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class CalendarOccurrence {
    private final WebElement webElement;

    public String getTitle() {
        return webElement.findElement(By.className("calendar-occurrence-title"))
            .getText();
    }
}
