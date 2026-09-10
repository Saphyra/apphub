package com.github.saphyra.apphub.integration.frontend.calendar.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarExpiredEventsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.*;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

public class ExpiredEventTest extends SeleniumTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(3);
    private static final LocalDate END_DATE = CURRENT_DATE.plusDays(3);

    @Test(groups = {"fe", "calendar"})
    public void hideExpiredEvent() {
        WebDriver driver = CalendarFlow.init(getServerPort(), extractDriver()).driver();

        createExpiredEvent(driver);

        CalendarIndexPageActions.toExpiredEventsPage(driver);

        AwaitilityWrapper.getListWithWait(() -> CalendarExpiredEventsPageActions.getEvents(driver), webElements -> !webElements.isEmpty())
            .getFirst()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(driver, By.id("calendar-opened-event")).isPresent())
            .assertTrue("Expired event is not opened.");

        CalendarExpiredEventsPageActions.hideExpiredEvent(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarExpiredEventsPageActions.getEvents(driver).isEmpty())
            .assertTrue("Expired event is not hidden.");
    }

    @Test(groups = {"fe", "calendar"})
    void extendExpiredEvent() {
        List<CalendarFlow.Context> contexts = CalendarFlow.init(getServerPort(), extractDrivers(2));
        CalendarFlow.Context ownerContext = contexts.get(0);
        CalendarFlow.Context sharedWithContext = contexts.get(1);
        WebDriver ownerDriver = ownerContext.driver();
        WebDriver sharedWithDriver = sharedWithContext.driver();

        CreateEventParameters event = createExpiredEvent(ownerDriver);
        shareEventWithUser(ownerDriver, event.getTitle(), sharedWithContext.userData().getEmail());

        verifyExpiredEventNotificationIsPresent(ownerDriver);

        verifyExpiredEventNotificationIsNotPresent(sharedWithDriver);

        CalendarIndexPageActions.toExpiredEventsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarExpiredEventsPageActions.getEvents(ownerDriver), webElements -> !webElements.isEmpty())
            .getFirst()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(ownerDriver, By.id("calendar-opened-event")).isPresent())
            .assertTrue("Expired event is not opened.");

        emptyExtendUntil(ownerDriver);

        CalendarExpiredEventsPageActions.setExtendUntil(ownerDriver, CURRENT_DATE.plusWeeks(2));
        CalendarExpiredEventsPageActions.extendExpiredEvent(ownerDriver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarExpiredEventsPageActions.getEvents(ownerDriver).isEmpty())
            .assertTrue("Expired event is not hidden.");
    }


    private static void emptyExtendUntil(WebDriver driver) {
        CalendarExpiredEventsPageActions.extendExpiredEvent(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_EMPTY_EXTEND_UNTIL_DATE);
    }

    private static void verifyExpiredEventNotificationIsPresent(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.expiredEventsButton(driver).isPresent())
            .assertTrue("Expired event notification is not present for owner.");
    }

    private static void verifyExpiredEventNotificationIsNotPresent(WebDriver driver) {
        WebElementUtils.waitForSpinnerToDisappear(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.expiredEventsButton(driver).isEmpty())
            .assertTrue("Expired event notification is present for shared-with user.");
    }

    private static void shareEventWithUser(WebDriver ownerDriver, String title, String email) {
        CalendarIndexPageActions.setReferenceDate(ownerDriver, CURRENT_DATE);

        AwaitilityWrapper.getListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, CURRENT_DATE), occurrences -> occurrences.stream().anyMatch(occurrence -> occurrence.getTitle().equals(title)))
            .stream()
            .filter(occurrence -> occurrence.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Occurrence not found: " + title))
            .open(ownerDriver);

        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);
        CalendarSharePageActions.selectUser(ownerDriver, email);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);
        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
    }

    private static CreateEventParameters createExpiredEvent(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters event = CreateEventParameters.valid(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of(CURRENT_DATE.getDayOfWeek()))
            .startDate(START_DATE)
            .endDate(END_DATE)
            .build();
        CalendarEventPageActions.fillForm(driver, event);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        return event;
    }
}
