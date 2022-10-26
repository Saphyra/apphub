package com.github.saphyra.apphub.integraton.frontend.diary;

import com.github.saphyra.apphub.integration.action.frontend.diary.DiaryActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.diary.CalendarSearchResult;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarSearchTest extends SeleniumTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final String TITLE = "title";

    @Test
    public void searchInDiary() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.DIARY);

        DiaryActions.createEvent(driver, CURRENT_DATE, TITLE);

        //Search in footer - Query too short
        DiaryActions.searchInFooter(driver, "as");

        NotificationUtil.verifyErrorNotification(driver, "A kereséshez írj be legalább 3 karaktert!");

        //Search in footer - No result
        DiaryActions.searchInFooter(driver, "asd");

        NotificationUtil.verifyErrorNotification(driver, "Nincs találat.");

        //Search - Success
        DiaryActions.searchInFooter(driver, TITLE);

        CalendarSearchResult searchResult = AwaitilityWrapper.getListWithWait(() -> DiaryActions.searchResult(driver), ts -> ts.size() == 1)
            .get(0);

        assertThat(searchResult.getTitle()).isEqualTo(TITLE);
        searchResult.openOccurrences();
        List<WebElement> occurrences = searchResult.getOccurrences();
        assertThat(occurrences).hasSize(1);
        assertThat(occurrences.get(0).getText()).isEqualTo(CURRENT_DATE.toString());

        //Search in result - Query too short
        DiaryActions.searchInResult(driver, "as");

        NotificationUtil.verifyErrorNotification(driver, "A kereséshez írj be legalább 3 karaktert!");

        //Search in result - No result
        DiaryActions.searchInResult(driver, "asd");

        NotificationUtil.verifyErrorNotification(driver, "Nincs találat.");

        //Search in result - Success
        DiaryActions.searchInResult(driver, CURRENT_DATE.toString());

        SleepUtil.sleep(1000); //Wait to refresh existing results

        searchResult = AwaitilityWrapper.getListWithWait(() -> DiaryActions.searchResult(driver), ts -> ts.size() == 1)
            .get(0);

        assertThat(searchResult.getTitle()).isEqualTo(TITLE);
        searchResult.openOccurrences();
        occurrences = searchResult.getOccurrences();
        assertThat(occurrences).hasSize(1);
        assertThat(occurrences.get(0).getText()).isEqualTo(CURRENT_DATE.toString());
    }
}
