package com.github.saphyra.apphub.integration.frontend.account;

import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.ChUsernamePasswordValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.ChangeUsernameParameters;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.ChangeUsernameValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.account.change_username.UsernameValidationResult;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.service.account.AccountPageActions;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChangeUsernameTest extends SeleniumTest {
    @DataProvider(name = "invalidParameters", parallel = true)
    public Object[][] invalidParametersProvider() {
        return new Object[][]{
            new Object[]{ChangeUsernameParameters.valid(), valid()},
            new Object[]{ChangeUsernameParameters.tooShortUsername(), tooShortUsername()},
            new Object[]{ChangeUsernameParameters.tooLongUsername(), tooLongUsername()},
            new Object[]{ChangeUsernameParameters.emptyPassword(), emptyPassword()}
        };
    }

    @Test(dataProvider = "invalidParameters")
    public void invalidParameters(ChangeUsernameParameters parameters, ChangeUsernameValidationResult validationResult) {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        AccountPageActions.fillChangeUsernameForm(driver, parameters);
        SleepUtil.sleep(2000);

        AccountPageActions.verifyChangeUsernameForm(driver, validationResult);
    }

    @Test
    public void usernameAlreadyExists() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters existingUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, existingUserData);
        ModulesPageActions.logout(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangeUsernameParameters parameters = ChangeUsernameParameters.valid()
            .toBuilder()
            .username(existingUserData.getUsername())
            .build();
        AccountPageActions.changeUsername(driver, parameters);

        NotificationUtil.verifyErrorNotification(driver, "A felhasználónév foglalt.");
    }

    @Test
    public void incorrectPassword() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangeUsernameParameters parameters = ChangeUsernameParameters.valid()
            .toBuilder()
            .password(DataConstants.INVALID_PASSWORD)
            .build();
        AccountPageActions.changeUsername(driver, parameters);

        NotificationUtil.verifyErrorNotification(driver, "Hibás jelszó.");
    }

    @Test
    public void successfulUsernameChange() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.MANAGE_ACCOUNT);

        ChangeUsernameParameters parameters = ChangeUsernameParameters.valid();
        AccountPageActions.changeUsername(driver, parameters);

        NotificationUtil.verifySuccessNotification(driver, "Felhasználónév megváltoztatva.");
    }

    private ChangeUsernameValidationResult valid() {
        return ChangeUsernameValidationResult.builder()
            .username(UsernameValidationResult.VALID)
            .password(ChUsernamePasswordValidationResult.VALID)
            .build();
    }

    private ChangeUsernameValidationResult tooShortUsername() {
        return valid()
            .toBuilder()
            .username(UsernameValidationResult.TOO_SHORT)
            .build();
    }

    private ChangeUsernameValidationResult tooLongUsername() {
        return valid()
            .toBuilder()
            .username(UsernameValidationResult.TOO_LONG)
            .build();
    }

    private ChangeUsernameValidationResult emptyPassword() {
        return valid()
            .toBuilder()
            .password(ChUsernamePasswordValidationResult.EMPTY_PASSWORD)
            .build();
    }
}
