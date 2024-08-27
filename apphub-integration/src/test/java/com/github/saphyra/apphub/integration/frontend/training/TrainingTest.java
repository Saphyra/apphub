package com.github.saphyra.apphub.integration.frontend.training;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.training.TrainingPageActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingTest extends SeleniumTest {
    private static final String PAGE_TITLE_PATTERN = "%s - %s - Oktatás - Apphub";
    private static final String CHAPTER_TITLE_PATTERN = "%s - %s";

    @DataProvider(name = "bookDataProvider", parallel = true)
    public Object[] bookDataProvider() {
        return Arrays.stream(ModuleLocation.values())
            .filter(moduleLocation -> Constants.MODULE_ID_TRAINING.equals(moduleLocation.getCategoryId()))
            .toArray();
    }

    @Test(dataProvider = "bookDataProvider", groups = {"fe", "training"})
    public void bookStepThroughTest(ModuleLocation moduleLocation) {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, moduleLocation);

        List<String> chapters = TrainingPageActions.getMenuItems(driver)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());

        checkPageContent(driver, 0, chapters, moduleLocation);

        int index;

        for (index = 1; TrainingPageActions.doesNextButtonExist(driver); index++) {
            TrainingPageActions.nextPage(driver);
            checkPageContent(driver, index, chapters, moduleLocation);
        }

        assertThat(index).isEqualTo(chapters.size());


        for (index = chapters.size() - 2; TrainingPageActions.doesPreviousButtonExist(driver); index--) {
            TrainingPageActions.previousPage(driver);
            checkPageContent(driver, index, chapters, moduleLocation);
        }

        assertThat(index).isEqualTo(-1);

        for (int i = chapters.size() - 1; i >= 0; i--) {
            TrainingPageActions.openMenuItem(driver, i);

            checkPageContent(driver, i, chapters, moduleLocation);
        }
    }

    private void checkPageContent(WebDriver driver, int index, List<String> chapters, ModuleLocation moduleLocation) {
        String bookTitle = moduleLocation.getLabel();
        String chapterTitle = chapters.get(index);

        String pageTitle = String.format(PAGE_TITLE_PATTERN, chapterTitle, bookTitle);
        String chapterHead = String.format(CHAPTER_TITLE_PATTERN, bookTitle, chapterTitle);

        assertThat(driver.getTitle()).isEqualTo(pageTitle);
        assertThat(TrainingPageActions.getChapterHead(driver).getText()).isEqualTo(chapterHead);
    }
}
