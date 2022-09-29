package com.github.saphyra.apphub.integraton.frontend.diary;

import com.github.saphyra.apphub.integration.action.frontend.diary.DiaryActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SortEventsTest extends SeleniumTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate FIRST_OF_MONTH = LocalDate.of(CURRENT_DATE.getYear(), CURRENT_DATE.getMonth(), 1);
    private static final String EXPIRED_TITLE = "expired-title";
    private static final String SNOOZED_TITLE = "snoozed-title";
    private static final String PENDING_TITLE = "pending-title";
    private static final String DONE_TITLE = "done-title";
    private static final String LATER_PENDING_TITLE = "later-pending-title";
    private static final String NO_TIME_PENDING_TITLE = "no-time-pending-title";

    @Test(groups = "diary")
    public void orderOfEvents() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.DIARY);

        //Create Expired event
        DiaryActions.previousMonth(driver);
        DiaryActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH.minusMonths(1));
        DiaryActions.fillEventTitle(driver, EXPIRED_TITLE);
        DiaryActions.createEvent(driver);

        SleepUtil.sleep(1000);

        //Create Snoozed event
        DiaryActions.nextMonth(driver);
        DiaryActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        DiaryActions.fillEventTitle(driver, SNOOZED_TITLE);
        DiaryActions.createEvent(driver);
        DiaryActions.openEvent(driver, SNOOZED_TITLE);
        DiaryActions.markAsSnoozed(driver);
        DiaryActions.closeViewEventPage(driver);

        //Create Done event
        DiaryActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        DiaryActions.fillEventTitle(driver, DONE_TITLE);
        DiaryActions.createEvent(driver);
        DiaryActions.openEvent(driver, DONE_TITLE);
        DiaryActions.markAsDone(driver);
        DiaryActions.closeViewEventPage(driver);

        //Create Pending event
        DiaryActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        DiaryActions.fillEventTitle(driver, PENDING_TITLE);
        DiaryActions.setCreateEventHours(driver, "10");
        DiaryActions.setCreateEventMinutes(driver, "10");
        DiaryActions.createEvent(driver);

        DiaryActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        DiaryActions.fillEventTitle(driver, LATER_PENDING_TITLE);
        DiaryActions.setCreateEventHours(driver, "10");
        DiaryActions.setCreateEventMinutes(driver, "11");
        DiaryActions.createEvent(driver);

        DiaryActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        DiaryActions.fillEventTitle(driver, NO_TIME_PENDING_TITLE);
        DiaryActions.createEvent(driver);

        SleepUtil.sleep(1000);

        //Check order
        List<String> dailyTasks = DiaryActions.getDailyTasks(driver)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
        assertThat(dailyTasks).containsExactly(EXPIRED_TITLE, PENDING_TITLE, LATER_PENDING_TITLE, NO_TIME_PENDING_TITLE, DONE_TITLE, SNOOZED_TITLE);

        List<String> calendarDayTasks = DiaryActions.getEventsOfDay(driver, CURRENT_DATE)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
        assertThat(calendarDayTasks).containsExactly(EXPIRED_TITLE, PENDING_TITLE, LATER_PENDING_TITLE, NO_TIME_PENDING_TITLE, DONE_TITLE, SNOOZED_TITLE);
    }
}
