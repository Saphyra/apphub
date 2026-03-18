package com.github.saphyra.apphub.integration.frontend.calendar.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.*;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import static com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters.DEFAULT_START_DATE;
import static org.assertj.core.api.Assertions.assertThat;

public class CalendarArchiveEventTest extends SeleniumTest {
    private static final String EVENT_TITLE_1 = "event-1";
    private static final String EVENT_TITLE_2 = "event-2";

    @Test(groups = {"fe", "calendar"})
    public void archiveEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        CalendarIndexPageActions.setReferenceDate(driver, DEFAULT_START_DATE);

        createEvent(driver, EVENT_TITLE_1);
        createEvent(driver, EVENT_TITLE_2);

        // open occurrence
        AwaitilityWrapper.getWithWait(() -> CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, DEFAULT_START_DATE, EVENT_TITLE_1))
            .orElseThrow(() -> new IllegalStateException("Occurrence not found"))
            .open(driver);

        // edit event and archive it
        CalendarIndexPageActions.editEvent(driver);
        CalendarIndexPageActions.setArchived(driver, true);
        CalendarEventPageActions.save(driver);
        CalendarEventPageActions.confirmSave(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_SAVED);

        // verify occurrence is rendered as archived
        AwaitilityWrapper.awaitAssert(() -> {
            CalendarOccurrence occurrence = CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, DEFAULT_START_DATE, EVENT_TITLE_1);
            assertThat(occurrence.isArchived()).isTrue();
        });

        // navigate to labels page and verify event order (non-archived first)
        CalendarIndexPageActions.toLabelsPage(driver);
        AwaitilityWrapper.awaitAssert(
            () -> assertThat(CalendarLabelsPageActions.getEvents(driver))
                .extracting(WebElement::getText)
                .containsExactly(EVENT_TITLE_2, EVENT_TITLE_1)
        );

        // select archived event and unarchive it
        CalendarLabelsPageActions.getEvent(driver, EVENT_TITLE_1)
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.toggleArchiveOpenedEvent(driver));

        AwaitilityWrapper.awaitAssert(() -> assertThat(WebElementUtils.getClasses(CalendarLabelsPageActions.getEvent(driver, EVENT_TITLE_1))).doesNotContain("archived"));

        // verify event order (event-1 should be first now)
        AwaitilityWrapper.awaitAssert(
            () -> assertThat(CalendarLabelsPageActions.getEvents(driver))
                .extracting(WebElement::getText)
                .containsExactly(EVENT_TITLE_1, EVENT_TITLE_2)
        );
    }

    private static void createEvent(WebDriver driver, String title) {
        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters event = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(title)
            .build();
        CalendarEventPageActions.fillForm(driver, event);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);
    }
}
