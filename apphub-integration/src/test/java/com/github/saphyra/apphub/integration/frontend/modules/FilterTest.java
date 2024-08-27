package com.github.saphyra.apphub.integration.frontend.modules;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.Category;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterTest extends SeleniumTest {
    @Test(groups = {"fe", "modules"})
    public void searchModule() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        noResult(driver);
        searchCategory(driver);
        searchByModule(driver);
    }

    private static void noResult(WebDriver driver) {
        ModulesPageActions.search(driver, "asd");
        AwaitilityWrapper.createDefault()
            .until(() -> ModulesPageActions.getCategories(driver).isEmpty());
    }

    private static void searchCategory(WebDriver driver) {
        ModulesPageActions.search(driver, "account");
        Category categoryResult = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() == 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("There is not only one category."));

        assertThat(categoryResult.getModules()).hasSize(1);
    }

    private static void searchByModule(WebDriver driver) {
        ModulesPageActions.search(driver, "a");
        AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() != 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Failed resetting search result."));

        ModulesPageActions.search(driver, "manage");
        Category moduleResult = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() == 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("There is not only one category."));

        assertThat(moduleResult.getModules()).hasSize(1);
    }
}
