package com.github.saphyra.apphub.integration.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarCreateEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarIndexLabelFilterTest extends SeleniumTest {
    private static final String LABEL = "label";
    private static final String LABELED_EVENT = "labeled-event";
    private static final LocalDate CURRENT_DATE = LocalDate.now();

    @Test(groups = {"fe", "calendar"})
    public void indexLabelFilter() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        CalendarIndexPageActions.openCreateEventPage(driver);
        CalendarCreateEventPageActions.fillForm(driver, CreateEventParameters.valid(RepetitionType.ONE_TIME).toBuilder().startDate(CURRENT_DATE).build());
        CalendarCreateEventPageActions.submit(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.openCreateEventPage(driver);
        CalendarCreateEventPageActions.fillForm(driver, CreateEventParameters.valid(RepetitionType.ONE_TIME).toBuilder().startDate(CURRENT_DATE).title(LABELED_EVENT).newLabels(List.of(LABEL)).build());
        CalendarCreateEventPageActions.submit(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.filterByLabel(driver, LABEL);

        CustomAssertions.singleListAssertThat(AwaitilityWrapper.getListWithWait(() -> CalendarIndexPageActions.getSelectedDayOccurrences(driver), webElements -> webElements.size() == 1))
            .returns(LABELED_EVENT, CalendarOccurrence::getTitle);

        CustomAssertions.singleListAssertThat(AwaitilityWrapper.getListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(driver, CURRENT_DATE), webElements -> webElements.size() == 1))
            .returns(LABELED_EVENT, CalendarOccurrence::getTitle);

        CalendarIndexPageActions.clearLabelFilter(driver);

        assertThat(AwaitilityWrapper.getListWithWait(() -> CalendarIndexPageActions.getSelectedDayOccurrences(driver), webElements -> webElements.size() == 2))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactlyInAnyOrder(CreateEventParameters.DEFAULT_TITLE, LABELED_EVENT);

        assertThat(AwaitilityWrapper.getListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(driver, CURRENT_DATE), webElements -> webElements.size() == 2))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactlyInAnyOrder(CreateEventParameters.DEFAULT_TITLE, LABELED_EVENT);
    }
}
