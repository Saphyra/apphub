package com.github.saphyra.apphub.integration.frontend.calendar.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.*;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarSearchEventTest extends SeleniumTest {
    private static final String SEARCH_TEXT = "search text";

    @Test(groups = {"fe", "calendar"})
    public void testSearchEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        createEventWithMatchingTitle(driver);
        createEventWithMatchingContent(driver);
        createEventWithMatchingOccurrence(driver);
        createUnmatchingEvent(driver);

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.openSearchPage(driver));
        CalendarSearchPageActions.setSearchText(driver, SEARCH_TEXT);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarSearchPageActions.getSearchResult(driver)).hasSize(3));
    }

    private void createUnmatchingEvent(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);
    }

    private void createEventWithMatchingOccurrence(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(driver, createEventParameters.getStartDate()));
        AwaitilityWrapper.getWithWait(() -> CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, createEventParameters.getStartDate(), createEventParameters.getTitle()))
            .orElseThrow(() -> new IllegalStateException("Occurrence not found"))
            .open(driver);
        CalendarIndexPageActions.editOccurrence(driver);

        CalendarOccurrencePageActions.setNote(driver, SEARCH_TEXT);
        CalendarOccurrencePageActions.save(driver);
    }

    private void createEventWithMatchingContent(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .content(SEARCH_TEXT)
            .build();
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);
    }

    private void createEventWithMatchingTitle(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters createEventParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(SEARCH_TEXT)
            .build();
        CalendarEventPageActions.fillForm(driver, createEventParameters);
        CalendarEventPageActions.create(driver);
    }
}
