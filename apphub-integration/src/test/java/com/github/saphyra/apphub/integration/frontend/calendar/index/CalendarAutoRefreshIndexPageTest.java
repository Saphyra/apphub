package com.github.saphyra.apphub.integration.frontend.calendar.index;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.HeadedSeleniumTest;
import com.github.saphyra.apphub.integration.core.driver.WebDriverMode;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarAutoRefreshIndexPageTest extends HeadedSeleniumTest {
    private static final String LABEL = "label";

    /**
     * Tests that creating an event automatically refreshes the calendar index page
     * and displays the new event, its label, and selected day occurrences correctly.
     *
     * <ul>
     *   <li>Create a one-time event with a label; verify it appears in the day's occurrences list after creation.</li>
     *   <li>Verify the newly created event is listed among selected day occurrences.</li>
     *   <li>Verify the label is displayed on the calendar.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar", "headed-only"})
    public void autoRefreshIndexPage() {
        WebDriver driver = extractDriver(WebDriverMode.HEADED);
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        CommonUtils.enableTestMode(driver);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);

        driver.switchTo()
            .newWindow(WindowType.TAB);
        driver.navigate().to(UrlFactory.create(getServerPort(), CalendarEndpoints.CALENDAR_PAGE));

        AwaitilityWrapper.createDefault()
            .until(() -> !WebElementUtils.isPresent(driver, By.className("spinner")))
            .assertTrue("Spinner is still loading");

        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters event = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .startDate(LocalDate.now())
            .newLabels(List.of(LABEL))
            .build();
        CalendarEventPageActions.fillForm(driver, event);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        AwaitilityWrapper.awaitAssert(() -> {
            CustomAssertions.singleListAssertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate()))
                .returns(event.getTitle(), CalendarOccurrence::getTitle);
            CustomAssertions.singleListAssertThat(CalendarIndexPageActions.getSelectedDayOccurrences(driver))
                .returns(event.getTitle(), CalendarOccurrence::getTitle);
            CustomAssertions.singleListAssertThat(CalendarIndexPageActions.getLabels(driver))
                .returns(LABEL, WebElement::getText);
        });
    }
}
