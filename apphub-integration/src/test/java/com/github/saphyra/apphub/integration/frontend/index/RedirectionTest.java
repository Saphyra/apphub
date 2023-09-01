package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.error.ErrorPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.api.ErrorMessageElement;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class RedirectionTest extends SeleniumTest {
    @Test(groups = {"fe", "index"})
    public void redirectToIndexWhenNoAccessToken() {
        //GIVEN
        WebDriver driver = extractDriver();

        //WHEN
        driver.navigate().to(UrlFactory.create(Endpoints.MODULES_PAGE));

        //THEN
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE, new HashMap<>(), CollectionUtils.singleValueMap("redirect", Endpoints.MODULES_PAGE)));
    }

    @Test(groups = {"fe", "index"})
    public void redirectToUrlAfterLogin() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.logout(driver);

        driver.navigate().to(UrlFactory.create(Endpoints.NOTEBOOK_PAGE));

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE, new HashMap<>(), CollectionUtils.singleValueMap("redirect", Endpoints.NOTEBOOK_PAGE)));

        IndexPageActions.submitLogin(driver, LoginParameters.fromRegistrationParameters(userData));

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.NOTEBOOK_PAGE)))
            .assertTrue("User is not redirected to notebook page.");
    }

    @Test(groups = {"fe", "index"})
    public void redirectToUrlAfterRegistration() {
        WebDriver driver = extractDriver();

        driver.navigate().to(UrlFactory.create(Endpoints.NOTEBOOK_PAGE));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE, new HashMap<>(), CollectionUtils.singleValueMap("redirect", Endpoints.NOTEBOOK_PAGE)));

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData, () -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.NOTEBOOK_PAGE)))
            .assertTrue("User is not redirected to notebook page.");
    }

    @Test(groups = {"fe", "index"})
    public void redirectToWebWhenCalledRoot() {
        //GIVEN
        WebDriver driver = extractDriver();

        //WHEN
        driver.navigate().to(UrlFactory.create("/"));

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoints.INDEX_PAGE));
    }

    @Test(groups = {"fe", "index"})
    public void autoLogin() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, RegistrationParameters.validParameters());

        Navigation.toIndexPage(driver, false);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    @Test(groups = {"fe", "index"})
    public void redirectToErrorPage() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        IndexPageActions.registerUser(driver, RegistrationParameters.validParameters());

        String url = UrlFactory.create(Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE);

        driver.navigate().to(url);

        assertThat(driver.getCurrentUrl()).startsWith(UrlFactory.create(Endpoints.ERROR_PAGE));

        ErrorMessageElement errorMessageElement = ErrorPageActions.getErrorMessage(driver);
        assertThat(errorMessageElement.getErrorCode()).isEqualTo(ErrorCode.MISSING_ROLE.name());
        assertThat(errorMessageElement.getErrorMessage()).isEqualTo(LocalizationProperties.getProperty(Language.ENGLISH, LocalizationKey.MISSING_ROLE));
    }
}