package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.common.framework.Endpoint;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.login.LoginParameters;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends SeleniumTest {
    private static final String PASSWORD = "password";
    private static final String EMPTY_CREDENTIALS_MESSAGE = "E-mail és jelszó megadása kötelező!";
    private static final String BAD_CREDENTIALS_MESSAGE = "Az email cím és jelszó kombinációja ismeretlen.";

    @DataProvider(name = "badCredentials", parallel = true)
    public Object[][] badCredentials() {
        return new Object[][]{
            new Object[]{emptyEmail(), EMPTY_CREDENTIALS_MESSAGE},
            new Object[]{emptyPassword(), EMPTY_CREDENTIALS_MESSAGE},
            new Object[]{unknownEmail(), BAD_CREDENTIALS_MESSAGE}
        };
    }

    @Test(dataProvider = "badCredentials")
    public void loginFailure(LoginParameters loginParameters, String errorMessage) {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        IndexPageActions.submitLogin(driver, loginParameters);

        NotificationUtil.verifyErrorNotification(driver, errorMessage);

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoint.WEB_ROOT));
    }

    @Test
    public void wrongPassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, registrationParameters);
        ModulesPageActions.logout(driver);

        LoginParameters loginParameters = LoginParameters.builder()
            .email(registrationParameters.getEmail())
            .password(PASSWORD)
            .build();
        IndexPageActions.submitLogin(driver, loginParameters);

        NotificationUtil.verifyErrorNotification(driver, BAD_CREDENTIALS_MESSAGE);

        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoint.WEB_ROOT));
    }

    private LoginParameters emptyEmail() {
        return LoginParameters.builder()
            .password(PASSWORD)
            .build();
    }

    private LoginParameters emptyPassword() {
        return LoginParameters.builder()
            .password(PASSWORD)
            .build();
    }

    private LoginParameters unknownEmail() {
        return LoginParameters.builder()
            .email(RegistrationParameters.validParameters().getEmail())
            .password(PASSWORD)
            .build();
    }
}
