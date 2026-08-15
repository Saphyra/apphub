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
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarViewSharedEventTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    void viewSharedEvent() {
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
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Verify event is visible with real data
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));
    }

    @Test(groups = {"fe", "calendar"})
    void seeSharedEvent() {
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
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Verify event is visible with real data
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(Constants.QUESTION_MARK + Constants.SHARED_SUFFIX));
    }

    @Test(groups = {"fe", "calendar"})
    void viewEventOfSharedLabel() {
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

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Verify event is visible with real data
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));

        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).extracting(WebElement::getText).containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));
    }

    @Test(groups = {"fe", "calendar"})
    void seeEventOfSharedLabel() {
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

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Verify event is visible with real data
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(Constants.QUESTION_MARK + Constants.SHARED_SUFFIX));

        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).extracting(WebElement::getText).containsExactly(Constants.QUESTION_MARK + Constants.SHARED_SUFFIX));
    }

    @Test(groups = {"fe", "calendar"})
    void viewEventOfSharedLabelWithSeeChildren() {
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

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Share event
        CalendarSharePageActions.back(ownerDriver);

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(ownerDriver, parameters.getTitle()))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.editOpenedEvent(ownerDriver));
        AwaitilityWrapper.retry(() -> CalendarEventPageActions.share(ownerDriver));

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.share(ownerDriver);

        //Verify event data is shared
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));

        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).extracting(WebElement::getText).containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));
    }

    @Test(groups = {"fe", "calendar"})
    void seeEventOfSharedLabelWithViewChildren() {
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

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Share event
        CalendarSharePageActions.back(ownerDriver);

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(ownerDriver, parameters.getTitle()))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.editOpenedEvent(ownerDriver));
        AwaitilityWrapper.retry(() -> CalendarEventPageActions.share(ownerDriver));

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE);
        CalendarSharePageActions.share(ownerDriver);

        //Verify event data is shared
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));

        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).extracting(WebElement::getText).containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));
    }
}
