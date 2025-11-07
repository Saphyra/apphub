package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.endpoints.ModulesEndpoints;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.LoginParameters;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.stream.Stream;

@Slf4j
public class LoginTest extends SeleniumTest {
    private static final String INCORRECT_PASSWORD = "incorrect-password";

    @Test(groups = {"fe", "index"})
    public void login() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);

        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, registrationParameters);
        ModulesPageActions.logout(getServerPort(), driver);

        emptyEmail(driver);
        emptyPassword(driver);
        incorrectEmail(driver, registrationParameters);
        incorrectPassword(driver, registrationParameters);
        LoginParameters loginParameters = successfulLogin(driver, registrationParameters);
        lockUser(driver, registrationParameters, loginParameters);
    }

    @Test(groups = {"fe", "index"})
    public void loginWithUsername() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);

        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, registrationParameters);
        ModulesPageActions.logout(getServerPort(), driver);

        IndexPageActions.login(getServerPort(), driver, LoginParameters.builder().userIdentifier(registrationParameters.getUsername()).password(registrationParameters.getPassword()).build());

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(ModulesEndpoints.MODULES_PAGE))
            .assertTrue("Login failed.");
    }

    private static void emptyEmail(WebDriver driver) {
        IndexPageActions.login(getServerPort(), driver, emptyEmail());
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_EMPTY_CREDENTIALS);
    }

    private static void emptyPassword(WebDriver driver) {
        IndexPageActions.login(getServerPort(), driver, emptyPassword());
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_EMPTY_CREDENTIALS);
    }

    private static void incorrectEmail(WebDriver driver, RegistrationParameters registrationParameters) {
        IndexPageActions.login(getServerPort(), driver, new LoginParameters(RegistrationParameters.validParameters().getEmail(), registrationParameters.getPassword()));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_BAD_CREDENTIALS);
    }

    private static void incorrectPassword(WebDriver driver, RegistrationParameters registrationParameters) {
        IndexPageActions.login(getServerPort(), driver, new LoginParameters(registrationParameters.getEmail(), INCORRECT_PASSWORD));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_BAD_CREDENTIALS);
    }

    private static LoginParameters successfulLogin(WebDriver driver, RegistrationParameters registrationParameters) {
        LoginParameters loginParameters = LoginParameters.fromRegistrationParameters(registrationParameters);
        IndexPageActions.login(getServerPort(), driver, loginParameters);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(ModulesEndpoints.MODULES_PAGE))
            .assertTrue("Login failed.");
        return loginParameters;
    }

    private static void lockUser(WebDriver driver, RegistrationParameters registrationParameters, LoginParameters loginParameters) {
        ModulesPageActions.logout(getServerPort(), driver);

        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                IndexPageActions.login(getServerPort(), driver, new LoginParameters(registrationParameters.getEmail(), INCORRECT_PASSWORD));
                ToastMessageUtil.verifyErrorToast(driver, LocalizedText.INDEX_BAD_CREDENTIALS);
            });

        IndexPageActions.login(getServerPort(), driver, new LoginParameters(registrationParameters.getEmail(), INCORRECT_PASSWORD));
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        IndexPageActions.login(getServerPort(), driver, loginParameters);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(registrationParameters.getEmail());

        IndexPageActions.login(getServerPort(), driver, loginParameters);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(ModulesEndpoints.MODULES_PAGE))
            .assertTrue("Login failed.");
    }

    private static LoginParameters emptyEmail() {
        return LoginParameters.builder()
            .password(INCORRECT_PASSWORD)
            .build();
    }

    private static LoginParameters emptyPassword() {
        return LoginParameters.builder()
            .password(INCORRECT_PASSWORD)
            .build();
    }
}
