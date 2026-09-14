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
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEditSharedEventTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";
    private static final String EVENT_TITLE_2 = "event-title-2";

    @Test(groups = {"fe", "calendar"})
    public void editSharedEvent_noViewGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        shareEventFromIndexPage(ownerDriver, parameters, testContext.sharedWithUserData().getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.SEE);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        editEventFromIndex(sharedWithDriver, parameters);

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle()));

        CalendarIndexPageActions.toLabelsPage(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver))
            .extracting(WebElement::getText)
            .containsExactly(parameters.getTitle()));
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedEvent_labelHasViewChildrenGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), false);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.share(ownerDriver);
        CalendarSharePageActions.back(ownerDriver);

        shareEventFromOpenedLabel(ownerDriver, parameters.getTitle(), testContext.sharedWithUserData().getEmail(), false);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT);
        CalendarSharePageActions.share(ownerDriver);

        editEventFromIndex(sharedWithDriver, parameters);

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, editedParameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, editedParameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(EVENT_TITLE_2 + Constants.SHARED_SUFFIX));

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getOpenedEventTitle(ownerDriver)).isEqualTo(EVENT_TITLE_2));
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedEvent() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        shareEventFromIndexPage(ownerDriver, parameters, testContext.sharedWithUserData().getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        editEventFromIndex(sharedWithDriver, parameters);

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

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
    public void editSharedEvent_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        shareEventFromIndexPage(ownerDriver, parameters, testContext.sharedWithUserData().getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT);
        CalendarSharePageActions.share(ownerDriver);
        CalendarSharePageActions.back(ownerDriver);

        editEventFromIndex(sharedWithDriver, parameters);

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        CalendarEventPageActions.cancelSave(sharedWithDriver);
        CalendarEventPageActions.backFromEdit(sharedWithDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle()));
    }

    @Test(groups = {"fe", "calendar"})
    public void editEventOfSharedLabel_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), true);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.share(ownerDriver);

        CalendarFlow.openSharedLabelEvent(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX, parameters.getTitle() + Constants.SHARED_SUFFIX);
        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.editOpenedEvent(sharedWithDriver));

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        CalendarEventPageActions.cancelSave(sharedWithDriver);
        CalendarEventPageActions.backFromEdit(sharedWithDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver))
            .extracting(WebElement::getText)
            .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));

        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver))
            .extracting(WebElement::getText)
            .containsExactly(parameters.getTitle()));

        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle()));
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedEventOfSharedLabel_onlyLabelHasGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), true);
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);

        shareEventFromIndexPage(ownerDriver, parameters, testContext.sharedWithUserData().getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT);
        CalendarSharePageActions.share(ownerDriver);
        CalendarSharePageActions.back(ownerDriver);

        CalendarFlow.openSharedLabelEvent(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX, parameters.getTitle() + Constants.SHARED_SUFFIX);
        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.editOpenedEvent(sharedWithDriver));

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver))
            .extracting(WebElement::getText)
            .containsExactly(EVENT_TITLE_2 + Constants.SHARED_SUFFIX));

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(EVENT_TITLE_2));
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedEventOfSharedLabel_onlyEventHasGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), true);
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);

        shareEventFromIndexPage(ownerDriver, parameters, testContext.sharedWithUserData().getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.EDIT);
        CalendarSharePageActions.share(ownerDriver);
        CalendarSharePageActions.back(ownerDriver);

        CalendarFlow.openSharedLabelEvent(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX, parameters.getTitle() + Constants.SHARED_SUFFIX);
        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.editOpenedEvent(sharedWithDriver));

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver))
            .extracting(WebElement::getText)
            .containsExactly(EVENT_TITLE_2 + Constants.SHARED_SUFFIX));

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(EVENT_TITLE_2));
    }

    private void shareEventFromIndexPage(WebDriver ownerDriver, CreateEventParameters parameters, String sharedWithEmail) {
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);

        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);
        CalendarSharePageActions.selectUser(ownerDriver, sharedWithEmail);
    }

    private void editEventFromIndex(WebDriver sharedWithDriver, CreateEventParameters parameters) {
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.editEvent(sharedWithDriver);
    }

    private void shareLabel(WebDriver ownerDriver, String sharedWithEmail, boolean useAnyLabel) {
        CalendarIndexPageActions.toLabelsPage(ownerDriver);
        if (useAnyLabel) {
            AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), labels -> !labels.isEmpty())
                .getFirst()
                .share(ownerDriver);
        } else {
            AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
                .orElseThrow()
                .share(ownerDriver);
        }

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithEmail);
    }

    private void shareEventFromOpenedLabel(WebDriver ownerDriver, String eventTitle, String sharedWithEmail, boolean useAnyLabel) {
        if (useAnyLabel) {
            AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), labels -> !labels.isEmpty())
                .getFirst()
                .open();
        } else {
            AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
                .orElseThrow()
                .open();
        }

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(ownerDriver, eventTitle))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.editOpenedEvent(ownerDriver));
        AwaitilityWrapper.retry(() -> CalendarEventPageActions.share(ownerDriver));
        CalendarSharePageActions.selectUser(ownerDriver, sharedWithEmail);
    }

}
