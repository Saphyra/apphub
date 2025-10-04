package com.github.saphyra.apphub.integration.frontend.calendar.index;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarIndexOccurrenceStatusTest extends SeleniumTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();

    @Test(groups = {"fe", "calendar"})
    public void occurrenceStatus() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        CommonUtils.enableTestMode(driver);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);

        CalendarIndexPageActions.openCreateEventPage(driver);
        CalendarEventPageActions.fillForm(driver, CreateEventParameters.valid(RepetitionType.ONE_TIME).toBuilder().startDate(CURRENT_DATE).build());
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, CURRENT_DATE, CreateEventParameters.DEFAULT_TITLE)
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOpenedOccurrenceTitle(driver)).isEqualTo(CreateEventParameters.DEFAULT_TITLE));

        //Done
        CalendarIndexPageActions.markOpenedOccurrenceDone(driver);
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, CURRENT_DATE, CreateEventParameters.DEFAULT_TITLE).getStatus()).isEqualTo(OccurrenceStatus.DONE);
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnSelectedDateValidated(driver, CreateEventParameters.DEFAULT_TITLE).getStatus()).isEqualTo(OccurrenceStatus.DONE);
        });

        //Snoozed
        CalendarIndexPageActions.markOpenedOccurrenceSnoozed(driver);
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, CURRENT_DATE, CreateEventParameters.DEFAULT_TITLE).getStatus()).isEqualTo(OccurrenceStatus.SNOOZED);
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnSelectedDateValidated(driver, CreateEventParameters.DEFAULT_TITLE).getStatus()).isEqualTo(OccurrenceStatus.SNOOZED);
        });

        //Reset
        CalendarIndexPageActions.resetOpenedOccurrenceStatus(driver);
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, CURRENT_DATE, CreateEventParameters.DEFAULT_TITLE).getStatus()).isEqualTo(OccurrenceStatus.PENDING);
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnSelectedDateValidated(driver, CreateEventParameters.DEFAULT_TITLE).getStatus()).isEqualTo(OccurrenceStatus.PENDING);
        });
    }
}
