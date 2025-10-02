package com.github.saphyra.apphub.integration.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarCreateEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
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
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarLabel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarLabelsTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";
    private static final String LABEL_2 = "label-2";

    @Test(groups = {"fe", "calendar"})
    public void labels() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        CalendarIndexPageActions.openCreateEventPage(driver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .newLabels(List.of(LABEL_1, LABEL_2))
            .build();
        CalendarCreateEventPageActions.fillForm(driver, parameters);
        CalendarCreateEventPageActions.submit(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.toLabelsPage(driver);
        assertThat(AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(driver), labels -> labels.size() == 2))
            .extracting(CalendarLabel::getLabel)
            .containsExactlyInAnyOrder(LABEL_1, LABEL_2);

        //Label exists
        CalendarLabelsPageActions.getLabel(driver, LABEL_1)
            .edit()
            .newLabel(driver, LABEL_2)
            .confirmNewLabel(driver)
            .run(() -> ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_LABEL_ALREADY_EXISTS))
            .run(() -> ToastMessageUtil.clearToasts(driver))
            .cancelNewLabel(driver);

        //Delete label
        CalendarLabelsPageActions.getLabel(driver, LABEL_2)
            .delete()
            .confirmDeletion(driver);
        AwaitilityWrapper.awaitAssert(() -> CustomAssertions.singleListAssertThat(CalendarLabelsPageActions.getLabels(driver)).returns(LABEL_1, CalendarLabel::getLabel));

        //Edit label
        CalendarLabelsPageActions.getLabel(driver, LABEL_1)
            .edit()
            .newLabel(driver, LABEL_2)
            .confirmNewLabel(driver);
        AwaitilityWrapper.awaitAssert(() -> CustomAssertions.singleListAssertThat(CalendarLabelsPageActions.getLabels(driver)).returns(LABEL_2, CalendarLabel::getLabel));

        CalendarLabelsPageActions.getLabel(driver, LABEL_2)
            .open();
        AwaitilityWrapper.awaitAssert(() -> CustomAssertions.singleListAssertThat(CalendarLabelsPageActions.getEvents(driver)).returns(CreateEventParameters.DEFAULT_TITLE, WebElement::getText));

        //Open event
        CalendarLabelsPageActions.getEvent(driver, CreateEventParameters.DEFAULT_TITLE)
            .click();
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getOpenedEventTitle(driver)).isEqualTo(CreateEventParameters.DEFAULT_TITLE));

        //Open occurrence
        WebElement occurrence = AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getOpenedEventOccurrences(driver), occurrences -> !occurrences.isEmpty())
            .get(0);
        LocalDate occurrenceDate = LocalDate.parse(occurrence.getText());
        occurrence.click();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getOpenedOccurrenceDate(driver)).isEqualTo(occurrenceDate));
    }
}
