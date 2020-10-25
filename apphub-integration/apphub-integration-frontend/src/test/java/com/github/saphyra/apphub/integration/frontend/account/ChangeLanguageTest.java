package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.account.AccountPageActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChangeLanguageTest extends SeleniumTest {
    @DataProvider(name = "languages", parallel = true)
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
