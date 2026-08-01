package com.github.saphyra.apphub.integration.frontend.calendar.share.temp;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarOccurrencePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
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

public class CalendarEditSharedEventTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";
    private static final String EVENT_TITLE_2 = "event-title-2";

    @Test(groups = {"fe", "calendar"})
    public void editSharedEvent() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver ownerDriver = drivers.get(0);
        WebDriver sharedWithDriver = drivers.get(1);

        //Create owner
        Navigation.toIndexPage(getServerPort(), ownerDriver);
        RegistrationParameters ownerUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(ownerDriver, ownerUserData);
        ModulesPageActions.openModule(getServerPort(), ownerDriver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(ownerDriver);

        //Create sharedWith
        Navigation.toIndexPage(getServerPort(), sharedWithDriver);
        RegistrationParameters sharedWithUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(sharedWithDriver, sharedWithUserData);
        ModulesPageActions.openModule(getServerPort(), sharedWithDriver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(sharedWithDriver);

        //Create event
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share event
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllOperations(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Edit event
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.editEvent(sharedWithDriver);

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        //Verify event edited
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, editedParameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(EVENT_TITLE_2 + Constants.SHARED_SUFFIX));

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, editedParameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(EVENT_TITLE_2));
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedEventOccurrence() {
        List<WebDriver> drivers = extractDrivers(2);
        WebDriver ownerDriver = drivers.get(0);
        WebDriver sharedWithDriver = drivers.get(1);

        //Create owner
        Navigation.toIndexPage(getServerPort(), ownerDriver);
        RegistrationParameters ownerUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(ownerDriver, ownerUserData);
        ModulesPageActions.openModule(getServerPort(), ownerDriver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(ownerDriver);

        //Create sharedWith
        Navigation.toIndexPage(getServerPort(), sharedWithDriver);
        RegistrationParameters sharedWithUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(sharedWithDriver, sharedWithUserData);
        ModulesPageActions.openModule(getServerPort(), sharedWithDriver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(sharedWithDriver);

        //Create event
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share event
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllOperations(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence edited
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, newOccurrenceDate)).hasSize(1));

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).hasSize(1));
    }
}
