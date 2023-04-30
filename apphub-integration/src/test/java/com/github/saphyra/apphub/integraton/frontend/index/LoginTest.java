package com.github.saphyra.apphub.integraton.frontend.index;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.structure.LoginParameters;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Stream;

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
        ToastMessageUtil.verifyErrorToast(driver, EMPTY_CREDENTIALS_MESSAGE);

        //Empty password
        IndexPageActions.submitLogin(driver, emptyPassword());
        ToastMessageUtil.verifyErrorToast(driver, EMPTY_CREDENTIALS_MESSAGE);

        //Incorrect e-mail
        IndexPageActions.submitLogin(driver, new LoginParameters(RegistrationParameters.validParameters().getEmail(), registrationParameters.getPassword()));
        ToastMessageUtil.verifyErrorToast(driver, BAD_CREDENTIALS_MESSAGE);

        //Incorrect-password
        IndexPageActions.submitLogin(driver, new LoginParameters(registrationParameters.getEmail(), INCORRECT_PASSWORD));
        ToastMessageUtil.verifyErrorToast(driver, BAD_CREDENTIALS_MESSAGE);

        //Successful login
        LoginParameters loginParameters = LoginParameters.fromRegistrationParameters(registrationParameters);
        IndexPageActions.submitLogin(driver, loginParameters);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.MODULES_PAGE))
            .assertTrue("Login failed.");

        //Lock user
        ModulesPageActions.logout(driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                IndexPageActions.submitLogin(driver, new LoginParameters(registrationParameters.getEmail(), INCORRECT_PASSWORD));
                ToastMessageUtil.verifyErrorToast(driver, BAD_CREDENTIALS_MESSAGE);
            });

        IndexPageActions.submitLogin(driver, new LoginParameters(registrationParameters.getEmail(), INCORRECT_PASSWORD));
        ToastMessageUtil.verifyErrorToast(driver, "Fiók zárolva. Próbáld újra később!");

        IndexPageActions.submitLogin(driver, loginParameters);
        ToastMessageUtil.verifyErrorToast(driver, "Fiók zárolva. Próbáld újra később!");

        DatabaseUtil.unlockUserByEmail(registrationParameters.getEmail());

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
