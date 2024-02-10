package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.action.frontend.account.AccountPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChangeLanguageTest extends SeleniumTest {
    @DataProvider(name = "languages")
    public Object[][] languages() {
        return new Object[][]{
            new Object[]{Language.ENGLISH},
            new Object[]{Language.HUNGARIAN}
        };
    }

    @Test(dataProvider = "languages", groups = {"fe", "account"})
    public void changeLanguage(Language language) {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.selectLanguage(driver, language);

        AwaitilityWrapper.createDefault()
            .until(() -> AccountPageActions.getActiveLanguage(driver) == language)
            .assertTrue("Language is not changed.");
    }
}
