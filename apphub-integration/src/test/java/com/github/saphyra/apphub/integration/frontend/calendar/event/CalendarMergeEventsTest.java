package com.github.saphyra.apphub.integration.frontend.calendar.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOpenedEventOccurrence;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarMergeEventsTest extends SeleniumTest {
    private static final LocalDate REFERENCE_DATE = LocalDate.now()
        .plusMonths(1)
        .withDayOfMonth(1);
    private static final String TITLE = "title";

    @Test(groups = {"fe", "calendar"})
    public void mergeEventsTest() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        createRepeatingEvent(driver);
        createParent(driver);
        createChild(driver);
        createOther(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !ToastMessageUtil.getAllToasts(driver).isEmpty())
            .assertTrue("No toast message found");

        AwaitilityWrapper.create(10, 1)
            .until(() -> ToastMessageUtil.getAllToasts(driver).isEmpty())
            .assertTrue("There are still toasts on the page");

        CalendarIndexPageActions.toLabelsPage(driver);
        CalendarLabelsPageActions.getEvent(driver, TITLE)
            .click();

        CalendarLabelsPageActions.mergeEvents(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarLabelsPageActions.getOpenedEventOccurrences(driver).size() == 2)
            .assertTrue("Events were not merged");

        assertThat(CalendarLabelsPageActions.getEvents(driver)).hasSize(3);

        assertThat(CalendarLabelsPageActions.getOpenedEventOccurrences(driver))
            .extracting(CalendarOpenedEventOccurrence::getDate)
            .containsExactlyInAnyOrder(
                REFERENCE_DATE,
                REFERENCE_DATE.plusDays(1)
            );
    }

    private void createOther(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .title("asd")
            .startDate(REFERENCE_DATE.plusDays(1))
            .build();
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);
    }

    private void createChild(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(TITLE + " ")
            .startDate(REFERENCE_DATE.plusDays(1))
            .build();
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);
    }

    private void createParent(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(TITLE)
            .startDate(REFERENCE_DATE)
            .build();
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);
    }

    private void createRepeatingEvent(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .title(TITLE + " ")
            .startDate(REFERENCE_DATE)
            .endDate(REFERENCE_DATE.plusWeeks(3))
            .build();
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);
    }
}
