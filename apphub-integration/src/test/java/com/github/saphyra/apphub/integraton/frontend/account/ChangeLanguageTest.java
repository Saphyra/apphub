package com.github.saphyra.apphub.integraton.frontend.account;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.account.AccountPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChangeLanguageTest extends SeleniumTest {
    @DataProvider(name = "languages")
    public Object[] languages() {
        return new Object[]{
            Language.ENGLISH,
            Language.HUNGARIAN
        };
    }

    @Test(dataProvider = "languages")
    public void changeLanguage(Language language) {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.selectLanguage(driver, language);

        NotificationUtil.verifySuccessNotification(driver, LocalizationProperties.getProperty(language, LocalizationKey.NOTIFICATION_LANGUAGE_CHANGED));

        AccountPageActions.verifyLanguageSelected(driver, language);
    }
}
