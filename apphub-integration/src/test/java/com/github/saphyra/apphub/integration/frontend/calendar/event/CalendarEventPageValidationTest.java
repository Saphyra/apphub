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
    /**
     * Tests validation of event creation fields.
     *
     * <ul>
     *   <li>Verifies that empty start date, end before start, blank title, and missing repetition days trigger errors.</li>
     * </ul>
     */
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
        endDateBeforeStartDate(driver);
        blankTitle(driver);
        daysOfWeek_noDaySelected(driver);
        daysOfMonth_noDaySelected(driver);
    }

    /**
     * Tests validation of label creation on the event page.
     *
     * <ul>
     *   <li>Attempts to add a too‑short label and expects an error toast.</li>
     *   <li>Attempts to add a too‑long label and expects an error toast.</li>
     * </ul>
     */
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
