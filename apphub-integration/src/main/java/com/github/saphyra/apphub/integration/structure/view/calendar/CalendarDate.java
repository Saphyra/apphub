package com.github.saphyra.apphub.integration.structure.view.calendar;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CalendarDate {
    private final WebDriver driver;
    private final WebElement webElement;

    public LocalDate getDate() {
        LocalDate referenceDate = CalendarIndexPageActions.getReferenceDate(driver);
        int day = Integer.parseInt(webElement.findElement(By.className("calendar-content-day-title")).getText());

        return referenceDate.withDayOfMonth(day);
    }

    public List<CalendarOccurrence> getOccurrences() {
        return webElement.findElements(By.className("calendar-occurrence"))
            .stream()
            .map(CalendarOccurrence::new)
            .toList();
    }
}
