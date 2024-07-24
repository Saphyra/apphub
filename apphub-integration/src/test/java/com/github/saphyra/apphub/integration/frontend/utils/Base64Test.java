package com.github.saphyra.apphub.integration.frontend.utils;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.utils.Base64Actions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64Test extends SeleniumTest {
    private static final String TEXT_TO_ENCODE = "text-to-encode";
    public static final String ENCODED_TEXT = Base64.getEncoder()
        .encodeToString(TEXT_TO_ENCODE.getBytes());

    @Test(groups = {"fe", "utils"})
    public void encodeDecodeBase64() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.UTILS_BASE_64);

        encode(driver);
        decode(driver);
    }

    private void decode(WebDriver driver) {
        Base64Actions.fillInput(driver, ENCODED_TEXT);
        Base64Actions.decode(driver);

        assertThat(Base64Actions.getOutput(driver)).isEqualTo(TEXT_TO_ENCODE);
    }

    private static void encode(WebDriver driver) {
        Base64Actions.fillInput(driver, TEXT_TO_ENCODE);
        Base64Actions.encode(driver);

        assertThat(Base64Actions.getOutput(driver)).isEqualTo(ENCODED_TEXT);
    }
}
