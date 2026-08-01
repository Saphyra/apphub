package com.github.saphyra.apphub.integration.frontend.calendar.share.temp;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
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

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarDeleteSharedEventTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    public void deleteSharedEvent() {
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

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);

        //Delete event
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.deleteEvent(sharedWithDriver);

        //Verify event deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).isEmpty());

        ownerDriver.navigate().refresh();

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()).isEmpty())
            .assertTrue();
    }

    @Test(groups = {"fe", "calendar"})
    public void deleteSharedEventOccurrence() {
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
            .repeatForDays(2)
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

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);

        //Delete occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.deleteOpenedOccurrence(sharedWithDriver);

        //Verify occurrence deleted
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).isEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate().plusDays(1))).hasSize(1);
        });

        ownerDriver.navigate().refresh();

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).isEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate().plusDays(1))).hasSize(1);
        });
    }
}
