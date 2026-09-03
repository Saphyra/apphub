package com.github.saphyra.apphub.integration.frontend.calendar.share.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateEventTest extends SeleniumTest {
    private static final String LABEL = "label";
    private static final String NEW_EVENT = "new-event";

    @Test(groups = {"fe", "calendar"})
    public void createEventToSharedLabel() {
        List<WebDriver> drivers = extractDrivers(2);

        List<CalendarFlow.Context> contexts = CalendarFlow.init(getServerPort(), drivers);
        WebDriver ownerDriver = contexts.get(0).driver();
        RegistrationParameters sharedWithUserData = contexts.get(1).userData();
        WebDriver sharedWithDriver = contexts.get(1).driver();

        //Create event
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver))
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        //Create event using shared label
        CalendarIndexPageActions.openCreateEventPage(sharedWithDriver);

        CreateEventParameters newParameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .title(NEW_EVENT)
            .existingLabels(List.of(LABEL + Constants.SHARED_SUFFIX))
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, newParameters);
        CalendarEventPageActions.create(sharedWithDriver);
        ToastMessageUtil.verifySuccessToast(sharedWithDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Verify event created
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, newParameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, newParameters.getStartDate())).hasSize(2));

        CalendarIndexPageActions.filterByLabel(sharedWithDriver, LABEL + Constants.SHARED_SUFFIX);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, newParameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactlyInAnyOrder(NEW_EVENT, parameters.getTitle() + Constants.SHARED_SUFFIX)
        );

        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newParameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newParameters.getStartDate())).hasSize(2));
        CalendarIndexPageActions.filterByLabel(ownerDriver, LABEL);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newParameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactlyInAnyOrder(parameters.getTitle(), NEW_EVENT + Constants.SHARED_SUFFIX)
        );
    }
}
