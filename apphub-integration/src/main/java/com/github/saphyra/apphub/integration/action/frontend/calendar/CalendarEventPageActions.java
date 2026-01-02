package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CalendarEventPageActions {
    public static void fillForm(WebDriver driver, CreateEventParameters parameters) {
        AwaitilityWrapper.createDefault()
            .until(() -> !WebElementUtils.isPresent(driver, By.className("spinner")))
            .assertTrue("Spinner is still loading");

        WebElementUtils.selectOptionByValue(driver.findElement(By.id("calendar-event-repetition-type")), parameters.getRepetitionType().name());

        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-event-start-date")), Optional.ofNullable(parameters.getStartDate()).map(LocalDate::toString).orElse(" "));
        WebElementUtils.getIfPresent(() -> driver.findElement(By.id("calendar-event-end-date")))
            .ifPresent(webElement -> WebElementUtils.clearAndFill(webElement, Optional.ofNullable(parameters.getEndDate()).map(LocalDate::toString).orElse(" ")));
        WebElementUtils.getIfPresent(() -> driver.findElement(By.id("calendar-event-time")))
            .ifPresent(webElement -> WebElementUtils.clearAndFill(webElement, parameters.getTime().format(DateTimeFormatter.ofPattern("hh:mm"))));
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-event-title")), parameters.getTitle());
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-event-content")), parameters.getContent());
        setRepetitionData(driver, parameters.getRepetitionType(), parameters.getRepetitionData());
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-event-remind-me-before-days")), parameters.getRemindMeBeforeDays());
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-event-repeat-for")), parameters.getRepeatForDays());
        addExistingLabels(driver, parameters.getExistingLabels());
        addNewLabels(driver, parameters.getNewLabels());
    }

    private static void addNewLabels(WebDriver driver, List<String> newLabels) {
        newLabels.forEach(label -> addNewLabel(driver, label));
    }

    public static void addNewLabel(WebDriver driver, String label) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-event-new-label")), label);
        driver.findElement(By.id("calendar-event-new-label-button"))
            .click();
    }

    private static void addExistingLabels(WebDriver driver, List<String> existingLabels) {
        existingLabels.forEach(label -> addLabel(driver, label));
    }

    private static void addLabel(WebDriver driver, String label) {
        driver.findElements(By.cssSelector(".calendar-event-available-labels .calendar-label"))
            .stream()
            .filter(element -> element.getText().equals(label))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Label not found: " + label))
            .click();
    }

    private static void setRepetitionData(WebDriver driver, RepetitionType repetitionType, Object repetitionData) {
        switch (repetitionType) {
            case ONE_TIME:
                break;
            case EVERY_X_DAYS:
                WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-event-repetition-data")), repetitionData);
                break;
            case DAYS_OF_WEEK:
                //noinspection unchecked
                List<DayOfWeek> daysOfWeek = (List<DayOfWeek>) repetitionData;
                Arrays.stream(DayOfWeek.values())
                    .forEach(dayOfWeek -> {
                        WebElement webElement = driver.findElement(By.cssSelector("#calendar-event-repetition-data input[value='%s']".formatted(dayOfWeek.name())));
                        boolean shouldBeChecked = daysOfWeek.contains(dayOfWeek);
                        if (webElement.isSelected() != shouldBeChecked) {
                            webElement.click();
                        }
                    });
                break;
            case DAYS_OF_MONTH:
                //noinspection unchecked
                List<Integer> daysOfMonth = (List<Integer>) repetitionData;
                driver.findElements(By.cssSelector("#calendar-event-repetition-data .multi-select-options input"))
                    .forEach(webElement -> {
                        Integer day = Integer.valueOf(webElement.getAttribute("value"));
                        boolean shouldBeChecked = daysOfMonth.contains(day);
                        if (webElement.isSelected() != shouldBeChecked) {
                            webElement.click();
                        }
                    });
                break;
            default:
                throw new IllegalArgumentException("Unsupported repetitionType " + repetitionType);
        }
    }

    public static void create(WebDriver driver) {
        driver.findElement(By.id("calendar-create-event-button"))
            .click();
    }

    public static void save(WebDriver driver) {
        driver.findElement(By.id("calendar-edit-event-save-button"))
            .click();
    }

    public static void confirmSave(WebDriver driver) {
        driver.findElement(By.id("calendar-edit-event-confirm-save-button"))
            .click();
    }

    public static void backFromEdit(WebDriver driver) {
        driver.findElement(By.id("calendar-edit-event-back-button"))
            .click();
    }

    public static void setCreateOccurrenceDate(WebDriver driver, LocalDate date) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-edit-event-new-occurrence-date")), date);
    }

    public static void createOccurrence(WebDriver driver) {
        driver.findElement(By.id("calendar-edit-event-create-occurrence-button"))
            .click();
    }
}
