package com.github.saphyra.apphub.integration.frontend.calendar.share.temp;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
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
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
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
    void deleteSharedEvent_noGrant() {
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
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.DELETE);
        CalendarSharePageActions.share(ownerDriver);

        CalendarSharePageActions.back(ownerDriver);

        //Delete event
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.deleteEvent(sharedWithDriver);

        //Verify event is not deleted

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).hasSize(1));

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));
    }

    @Test(groups = {"fe", "calendar"})
    void deleteEventOfSharedLabel_noGrant() {
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

        //Create event with label
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.DELETE_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Delete event
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(sharedWithDriver, parameters.getTitle() + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is not deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).hasSize(1));

        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).hasSize(1));

        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));
    }

    @Test(groups = {"fe", "calendar"})
    void deleteSharedEventOfSharedLabel_onlyLabelHasGrant() {
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

        //Create event with label
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Share event
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.DELETE);
        CalendarSharePageActions.share(ownerDriver);

        CalendarSharePageActions.back(ownerDriver);

        //Delete event
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(sharedWithDriver, parameters.getTitle() + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).isEmpty());

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    void deleteSharedEventOfSharedLabel_onlyEventHasGrant() {
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

        //Create event with label
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.DELETE_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Share event
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        CalendarSharePageActions.back(ownerDriver);

        //Delete event
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(sharedWithDriver, parameters.getTitle() + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).isEmpty());

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    void deleteSharedLabelEvent() {
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

        //Create event with label
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Delete event
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(sharedWithDriver, parameters.getTitle() + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).isEmpty());

        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).isEmpty();

        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).isEmpty());
    }

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
        CalendarSharePageActions.selectAllGrants(ownerDriver);
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
}
