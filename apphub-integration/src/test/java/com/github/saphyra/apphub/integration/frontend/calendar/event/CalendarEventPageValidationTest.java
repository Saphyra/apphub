package com.github.saphyra.apphub.integration.frontend.calendar.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

public class CalendarEventPageValidationTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    public void eventPageValidation() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        CalendarIndexPageActions.openCreateEventPage(driver);

        emptyStartDate(driver);
        emptyEndDate(driver);
        endDateBeforeStartDate(driver);
        blankTitle(driver);
        repeatForDaysTooLow(driver);
        everyXDays_repetitionDataTooLow(driver);
        daysOfWeek_noDaySelected(driver);
        daysOfMonth_noDaySelected(driver);
    }

    @Test(groups = {"fe", "calendar"})
    public void createEventLabelValidation() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        CalendarIndexPageActions.openCreateEventPage(driver);

        labelTooShort(driver);
        labelTooLong(driver);
        labelAlreadyExists(driver);
    }

    private void labelAlreadyExists(WebDriver driver) {
        CalendarEventPageActions.addNewLabel(driver, LABEL_1);
        CalendarEventPageActions.addNewLabel(driver, LABEL_1);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_LABEL_ALREADY_EXISTS);
        ToastMessageUtil.clearToasts(driver);
    }

    private void labelTooLong(WebDriver driver) {
        String label = "a".repeat(256);

        CalendarEventPageActions.addNewLabel(driver, label);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_LABEL_TOO_LONG);
        ToastMessageUtil.clearToasts(driver);
    }

    private void labelTooShort(WebDriver driver) {
        CalendarEventPageActions.addNewLabel(driver, " ");

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_LABEL_TOO_SHORT);
        ToastMessageUtil.clearToasts(driver);
    }

    private void daysOfMonth_noDaySelected(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.DAYS_OF_MONTH)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_NO_DAYS_DEFINED);
        ToastMessageUtil.clearToasts(driver);
    }

    private void daysOfWeek_noDaySelected(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of())
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_NO_DAYS_DEFINED);
        ToastMessageUtil.clearToasts(driver);
    }

    private void everyXDays_repetitionDataTooLow(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .repetitionData(0)
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_EVERY_X_DAYS_REPETITION_DATA_TOO_LOW);
        ToastMessageUtil.clearToasts(driver);
    }

    private void repeatForDaysTooLow(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .repeatForDays(0)
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_REPEAT_FOR_DAYS_TOO_LOW);
        ToastMessageUtil.clearToasts(driver);
    }

    private void blankTitle(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(" ")
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_BLANK_TITLE);
        ToastMessageUtil.clearToasts(driver);
    }

    private void endDateBeforeStartDate(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .endDate(CreateEventParameters.DEFAULT_START_DATE.minusDays(1))
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_END_DATE_BEFORE_START_DATE);
        ToastMessageUtil.clearToasts(driver);
    }

    private void emptyEndDate(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .endDate(null)
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_EMPTY_END_DATE);
        ToastMessageUtil.clearToasts(driver);
    }

    private void emptyStartDate(WebDriver driver) {
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .startDate(null)
            .build();

        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_EMPTY_START_DATE);
        ToastMessageUtil.clearToasts(driver);
    }
}
