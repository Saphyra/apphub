package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.action.frontend.error.ErrorPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.AdminPanelEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
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
        driver.navigate().to(UrlFactory.create(getServerPort(), ModulesEndpoints.MODULES_PAGE));

        //THEN
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(getServerPort(), GenericEndpoints.INDEX_PAGE, new HashMap<>(), CollectionUtils.singleValueMap("redirect", ModulesEndpoints.MODULES_PAGE)));
    }

    @Test(groups = {"fe", "index"})
    public void redirectToUrlAfterLogin() {
        WebDriver driver = extractDriver();
        Integer serverPort = getServerPort();
        Navigation.toIndexPage(serverPort, driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.logout(serverPort, driver);

        driver.navigate().to(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_PAGE));

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(serverPort, GenericEndpoints.INDEX_PAGE, new HashMap<>(), CollectionUtils.singleValueMap("redirect", NotebookEndpoints.NOTEBOOK_PAGE)));

        IndexPageActions.login(serverPort, driver, LoginParameters.fromRegistrationParameters(userData));

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_PAGE)))
            .assertTrue("User is not redirected to notebook page.");
    }

    @Test(groups = {"fe", "index"})
    public void redirectToUrlAfterRegistration() {
        WebDriver driver = extractDriver();

        Integer serverPort = getServerPort();
        driver.navigate().to(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_PAGE));
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(serverPort, GenericEndpoints.INDEX_PAGE, new HashMap<>(), CollectionUtils.singleValueMap("redirect", NotebookEndpoints.NOTEBOOK_PAGE)));

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData, () -> driver.getCurrentUrl().endsWith(NotebookEndpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_PAGE)))
            .assertTrue("User is not redirected to notebook page.");
    }

    @Test(groups = {"fe", "index"})
    public void redirectToWebWhenCalledRoot() {
        //GIVEN
        WebDriver driver = extractDriver();

        //WHEN
        driver.navigate().to(UrlFactory.create(getServerPort(), "/"));

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(getServerPort(), GenericEndpoints.INDEX_PAGE));
    }

    @Test(groups = {"fe", "index"})
    public void autoLogin() {
        WebDriver driver = extractDriver();

        Integer serverPort = getServerPort();
        Navigation.toIndexPage(serverPort, driver);
        IndexPageActions.registerUser(driver, RegistrationParameters.validParameters());

        Navigation.toIndexPage(serverPort, driver, false);

        AwaitilityWrapper.awaitAssert(driver::getCurrentUrl, currentUrl -> assertThat(currentUrl).isEqualTo(UrlFactory.create(serverPort, ModulesEndpoints.MODULES_PAGE)));
    }

    @Test(groups = {"fe", "index"})
    public void redirectToErrorPage() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);

        IndexPageActions.registerUser(driver, RegistrationParameters.validParameters());

        String url = UrlFactory.create(getServerPort(), AdminPanelEndpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE);

        driver.navigate().to(url);

        assertThat(driver.getCurrentUrl()).startsWith(UrlFactory.create(getServerPort(), GenericEndpoints.ERROR_PAGE));

        assertThat(ErrorPageActions.getErrorCode(driver)).isEqualTo(ErrorCode.MISSING_ROLE.name());
        assertThat(ErrorPageActions.getErrorMessage(driver)).isEqualTo(LocalizedText.ERROR_MISSING_ROLE.getText());
    }
}