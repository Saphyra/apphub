package com.github.saphyra.apphub.integration.frontend.modules;

import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.model.modules.Category;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterTest extends SeleniumTest {
    @Test
    public void searchByCategory() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.search(driver, "asd");
        AwaitilityWrapper.createDefault()
            .until(() -> ModulesPageActions.getCategories(driver).isEmpty());

        ModulesPageActions.search(driver, "kok");
        Category category = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() == 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("There is not only one category."));

        assertThat(category.getModules()).hasSize(1);
    }

    @Test
    public void searchByModule() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.search(driver, "asd");
        AwaitilityWrapper.createDefault()
            .until(() -> ModulesPageActions.getCategories(driver).isEmpty());

        ModulesPageActions.search(driver, "ók");
        Category category = AwaitilityWrapper.getListWithWait(() -> ModulesPageActions.getCategories(driver), categories -> categories.size() == 1)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("There is not only one category."));

        assertThat(category.getModules()).hasSize(1);
    }
}
