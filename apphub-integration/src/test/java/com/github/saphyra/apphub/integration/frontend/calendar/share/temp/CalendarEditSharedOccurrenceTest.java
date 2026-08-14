package com.github.saphyra.apphub.integration.frontend.calendar.share.temp;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarOccurrencePageActions;
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
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEditSharedOccurrenceTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    public void editSharedOccurrence() {
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

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share occurrence
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editOpenedOccurrence(ownerDriver);
        CalendarOccurrencePageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
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
        CalendarOccurrencePageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).hasSize(1));
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedEvent() {
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

    @Test(groups = {"fe", "calendar"})
    void editOccurrenceOfSharedLabel() {
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

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())
            .getFirst()
            .open(sharedWithDriver);
        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence edited
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, newOccurrenceDate)).hasSize(1));

        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).hasSize(1));
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedOccurrence_noEditGrant() {
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

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share occurrence
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editOpenedOccurrence(ownerDriver);
        CalendarOccurrencePageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT);
        CalendarSharePageActions.share(ownerDriver);

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarOccurrencePageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedOccurrence_noViewGrant() {
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

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share occurrence
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editOpenedOccurrence(ownerDriver);
        CalendarOccurrencePageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.share(ownerDriver);

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarOccurrencePageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedEvent_noEditGrant() {
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

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share event
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.editEvent(ownerDriver));
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedEvent_noViewGrant() {
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

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share event
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.editEvent(ownerDriver));
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedLabel_noEditGrant() {
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
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())
            .getFirst()
            .open(sharedWithDriver);
        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedLabel_noViewGrant() {
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
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())
            .getFirst()
            .open(sharedWithDriver);
        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(dataProvider = "lego", groups = {"fe", "calendar"})
    public void editSharedOccurrence_legoGrants(Grant labelGrant, Grant eventGrant, Grant occurrenceGrant) {
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
        if(nonNull(labelGrant)){
            //Share label
            CalendarIndexPageActions.toLabelsPage(ownerDriver);

            AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
                .getFirst()
                .share(ownerDriver);

            CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
            CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
            CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE_CHILDREN);
            CalendarSharePageActions.toggleGrant(ownerDriver, labelGrant);
            CalendarSharePageActions.share(ownerDriver);

            CalendarSharePageActions.back(ownerDriver);
            CalendarLabelsPageActions.back(ownerDriver);
        }

        //Share event
        if(nonNull(eventGrant)){
            CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

            AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
                .open(ownerDriver);

            AwaitilityWrapper.retry(() -> CalendarIndexPageActions.editEvent(ownerDriver));
            CalendarEventPageActions.share(ownerDriver);

            CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
            CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
            CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE_CHILDREN);
            CalendarSharePageActions.toggleGrant(ownerDriver, eventGrant);
            CalendarSharePageActions.share(ownerDriver);

            CalendarSharePageActions.back(ownerDriver);
            CalendarEventPageActions.backFromEdit(ownerDriver);
        }

        if(nonNull(occurrenceGrant)){
            //Share occurrence
            CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

            AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
                .open(ownerDriver);

            CalendarIndexPageActions.editOpenedOccurrence(ownerDriver);
            CalendarOccurrencePageActions.share(ownerDriver);

            CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
            CalendarSharePageActions.toggleGrant(ownerDriver, occurrenceGrant);
            CalendarSharePageActions.share(ownerDriver);

            CalendarSharePageActions.back(ownerDriver);
            CalendarOccurrencePageActions.back(ownerDriver);
        }

        //Edit occurrence
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())
            .getFirst()
            .open(sharedWithDriver);
        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = parameters.getStartDate().plusDays(1);

        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);

        //Verify occurrence edited
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, newOccurrenceDate)).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).hasSize(1));
    }

    //TODO lego grants from multiple shares
    @DataProvider(parallel = true)
    private static Object[][] lego() {
        return new Object[][]{
            new Object[]{Grant.EDIT_CHILDREN, Grant.VIEW_CHILDREN, null},
            new Object[]{Grant.EDIT_CHILDREN, null, Grant.VIEW},
            new Object[]{Grant.VIEW_CHILDREN, null, Grant.EDIT},
            new Object[]{Grant.VIEW_CHILDREN, Grant.EDIT_CHILDREN, null},
            new Object[]{null, Grant.VIEW_CHILDREN, Grant.EDIT},
            new Object[]{null, Grant.EDIT_CHILDREN, Grant.VIEW},
        };
    }
}
