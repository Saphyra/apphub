package com.github.saphyra.apphub.integration.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
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
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarLabel;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOpenedEventOccurrence;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static com.github.saphyra.apphub.integration.action.backend.calendar.EventRequestFactory.NEW_TITLE;
import static org.assertj.core.api.Assertions.assertThat;

public class CalendarLabelsTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";
    private static final String LABEL_2 = "label-2";

    /**
     * Tests creation, editing and deletion of calendar labels.
     *
     * <ul>
     *   <li>Creates an event with two labels.</li>
     *   <li>Verifies that both labels appear in the label list.</li>
     *   <li>Deletes one label and checks it is removed.</li>
     *   <li>Edits the remaining label to a new name and verifies the change.</li>
     *   <li>Opens an event via its label and confirms the correct title.</li>
     *   <li>Opens an occurrence from the label view and checks the date matches.</li>
     * </ul>
     */
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
        CalendarEventPageActions.fillForm(driver, parameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.toLabelsPage(driver);
        assertThat(AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(driver), labels -> labels.size() == 2))
            .extracting(CalendarLabel::getLabel)
            .containsExactlyInAnyOrder(LABEL_1, LABEL_2);

        //Delete label
        CalendarLabelsPageActions.getLabel(driver, LABEL_2)
            .delete(driver)
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
        CalendarOpenedEventOccurrence occurrence = AwaitilityWrapper.getSingleItemFromListWithWait(
            () -> CalendarLabelsPageActions.getOpenedEventOccurrences(driver),
            occ -> occ.stream().findFirst()
        );
        LocalDate occurrenceDate = occurrence.getDate();
        occurrence.open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getOpenedOccurrenceDate(driver)).isEqualTo(occurrenceDate));
    }

    /**
     * Verifies that events without a label are correctly shown when the "No Label" filter is applied.
     *
     * <ul>
     *   <li>Creates an event with a label.</li>
     *   <li>Creates another event without any labels.</li>
     *   <li>Applies the "No Label" filter and checks only the unlabeled event appears.
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    public void getLabelsWithoutLabel() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        //Event with label
        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters withLabelParameters = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(driver, withLabelParameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Event without label
        WebElementUtils.waitForSpinnerToDisappear(driver);

        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters withoutLabelParameters = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .title(NEW_TITLE)
            .build();
        CalendarEventPageActions.fillForm(driver, withoutLabelParameters);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Verify filter
        CalendarIndexPageActions.toLabelsPage(driver);
        CalendarLabelsPageActions.selectNoLabelFilter(driver);

        AwaitilityWrapper.awaitAssert(() -> CustomAssertions.singleListAssertThat(CalendarLabelsPageActions.getEvents(driver)).returns(NEW_TITLE, WebElement::getText));
    }
}
