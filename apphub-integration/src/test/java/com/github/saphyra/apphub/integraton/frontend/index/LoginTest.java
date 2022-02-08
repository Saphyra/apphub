package com.github.saphyra.apphub.integraton.frontend.index;

import com.github.saphyra.apphub.integration.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.LoginParameters;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class LoginTest extends SeleniumTest {
    private static final String INCORRECT_PASSWORD = "incorrect-password";
    private static final String EMPTY_CREDENTIALS_MESSAGE = "E-mail és jelszó megadása kötelező!";
    private static final String BAD_CREDENTIALS_MESSAGE = "Az email cím és jelszó kombinációja ismeretlen.";

    @Test
    public void login() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, registrationParameters);
        ModulesPageActions.logout(driver);

        //Empty email
        IndexPageActions.submitLogin(driver, emptyEmail());
        NotificationUtil.verifyErrorNotification(driver, EMPTY_CREDENTIALS_MESSAGE);
        NotificationUtil.clearNotifications(driver);

        //Empty password
        IndexPageActions.submitLogin(driver, emptyPassword());
        NotificationUtil.verifyErrorNotification(driver, EMPTY_CREDENTIALS_MESSAGE);
        NotificationUtil.clearNotifications(driver);

        //Incorrect e-mail
        IndexPageActions.submitLogin(driver, new LoginParameters(RegistrationParameters.validParameters().getEmail(), registrationParameters.getPassword()));
        NotificationUtil.verifyErrorNotification(driver, BAD_CREDENTIALS_MESSAGE);
        NotificationUtil.clearNotifications(driver);

        //Incorrect-password
        IndexPageActions.submitLogin(driver, new LoginParameters(registrationParameters.getEmail(), INCORRECT_PASSWORD));
        NotificationUtil.verifyErrorNotification(driver, BAD_CREDENTIALS_MESSAGE);
        NotificationUtil.clearNotifications(driver);

        //Successful login
        LoginParameters loginParameters = LoginParameters.fromRegistrationParameters(registrationParameters);
        IndexPageActions.submitLogin(driver, loginParameters);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.MODULES_PAGE))
            .assertTrue("Login failed.");
    }

    private LoginParameters emptyEmail() {
        return LoginParameters.builder()
            .password(INCORRECT_PASSWORD)
            .build();
    }

    private LoginParameters emptyPassword() {
        return LoginParameters.builder()
            .password(INCORRECT_PASSWORD)
            .build();
    }
}
