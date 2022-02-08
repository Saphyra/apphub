package com.github.saphyra.apphub.integraton.frontend.index;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.error.ErrorPageActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.ErrorMessageElement;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class RedirectionTest extends SeleniumTest {
    @Test
    public void redirectToIndexWhenNoAccessToken() {
        //GIVEN
        WebDriver driver = extractDriver();

        //WHEN
        driver.navigate().to(UrlFactory.create(SERVER_PORT, Endpoints.MODULES_PAGE));

        //THEN
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(SERVER_PORT, Endpoints.INDEX_PAGE));
    }

    @Test
    public void redirectToWebWhenCalledRoot() {
        //GIVEN
        WebDriver driver = extractDriver();

        //WHEN
        driver.navigate().to(UrlFactory.create(SERVER_PORT, "/"));

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(SERVER_PORT, Endpoints.INDEX_PAGE));
    }

    @Test
    public void autoLogin() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, RegistrationParameters.validParameters());

        Navigation.toIndexPage(driver, false);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoints.MODULES_PAGE)))
            .assertTrue();
    }

    @Test
    public void redirectToErrorPage() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        IndexPageActions.registerUser(driver, RegistrationParameters.validParameters());

        String url = UrlFactory.create(Endpoints.ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE);

        driver.navigate().to(url);

        assertThat(driver.getCurrentUrl()).startsWith(UrlFactory.create(Endpoints.ERROR_PAGE));

        ErrorMessageElement errorMessageElement = ErrorPageActions.getErrorMessage(driver);
        assertThat(errorMessageElement.getErrorCode()).isEqualTo(ErrorCode.MISSING_ROLE.name());
        assertThat(errorMessageElement.getErrorMessage()).isEqualTo(LocalizationProperties.getProperty(Language.HUNGARIAN, LocalizationKey.MISSING_ROLE));
    }
}