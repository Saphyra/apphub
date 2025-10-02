package com.github.saphyra.apphub.integration.frontend.calendar.index;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarView;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarIndexViewSelectorTest extends SeleniumTest {
    @Test(groups = {"fe", "calendar"})
    public void viewSelector() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);

        CalendarIndexPageActions.setView(driver, CalendarView.WEEK);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getDays(driver)).hasSize(7));

        CalendarIndexPageActions.setView(driver, CalendarView.SURROUNDING_WEEKS);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getDays(driver)).hasSize(21));

        CalendarIndexPageActions.setView(driver, CalendarView.MONTH);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getDays(driver).size()).isGreaterThanOrEqualTo(28));
    }
}
