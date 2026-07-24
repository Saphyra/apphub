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
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarShareIndexPageTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    void sharedLabel() {
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
        CalendarSharePageActions.selectAllOperations(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Verify shared label displayed
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        CalendarOccurrence occurrence = AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getDay(sharedWithDriver, parameters.getStartDate()).getOccurrences());

        assertThat(occurrence.isShared()).isTrue();
    }

    @Test(groups = {"fe", "calendar"})
    void sharedEvent() {
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

        //Create event to share
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share event
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getDay(ownerDriver, parameters.getStartDate()).getOccurrences())
            .open(ownerDriver);
        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.getOpenedOccurrenceTitle(ownerDriver).equals(parameters.getTitle()))
            .assertTrue("Occurrence is not opened");
        CalendarIndexPageActions.editEvent(ownerDriver);

        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllOperations(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Verify shared event displayed
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        CalendarOccurrence occurrence = AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getDay(sharedWithDriver, parameters.getStartDate()).getOccurrences());

        assertThat(occurrence.isShared()).isTrue();
    }
}
