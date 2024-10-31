package com.github.saphyra.apphub.integration.frontend.modules;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CollapseModulesTest extends SeleniumTest {
    private static final String CATEGORY_ID = "accounts";

    @Test(groups = {"fe", "modules"})
    public void collapseModules() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.findCategoryByIdValidated(driver, CATEGORY_ID)
            .toggleCollapse();

        AwaitilityWrapper.awaitAssert(() -> ModulesPageActions.findCategoryByIdValidated(driver, CATEGORY_ID), category -> assertThat(category.getModules()).isEmpty());

        ModulesPageActions.findCategoryByIdValidated(driver, CATEGORY_ID)
            .toggleCollapse();

        AwaitilityWrapper.awaitAssert(() -> ModulesPageActions.findCategoryByIdValidated(driver, CATEGORY_ID), category -> assertThat(category.getModules()).isNotEmpty());
    }
}
