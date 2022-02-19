package com.github.saphyra.apphub.integraton.frontend.modules;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.modules.Category;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterTest extends SeleniumTest {
    @Test
    public void searchModule() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        //No result
        ModulesPageActions.search(driver, "asd");
        AwaitilityWrapper.createDefault()
            .until(() -> ModulesPageActions.getCategories(driver).isEmpty());

        //Search category
        ModulesPageActions.search(driver, "kok");
        Category categoryResult = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() == 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("There is not only one category."));

        assertThat(categoryResult.getModules()).hasSize(1);

        //Search by module
        ModulesPageActions.search(driver, "ó");
        AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() != 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Failed resetting search result."));

        ModulesPageActions.search(driver, "ók");
        Category moduleResult = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() == 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("There is not only one category."));

        assertThat(moduleResult.getModules()).hasSize(1);
    }
}
