package com.github.saphyra.apphub.integration.frontend.index;

import com.github.saphyra.apphub.integration.common.framework.Endpoint;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RedirectionTest extends SeleniumTest {
    @Test
    public void redirectToIndexWhenNoAccessToken() {
        //GIVEN
        WebDriver driver = extractDriver();

        //WHEN
        driver.navigate().to(UrlFactory.create(SERVER_PORT, Endpoint.MODULES_PAGE));

        //THEN
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(SERVER_PORT, Endpoint.WEB_ROOT));
    }

    @Test
    public void redirectToWebWhenCalledRoot() {
        //GIVEN
        WebDriver driver = extractDriver();

        //WHEN
        driver.navigate().to(UrlFactory.create(SERVER_PORT, Endpoint.ROOT));


        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(SERVER_PORT, Endpoint.WEB_ROOT));
    }

    @Test
    public void autoLogin() {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(driver);
        IndexPageActions.registerUser(driver, RegistrationParameters.validParameters());

        Navigation.toIndexPage(driver);

        assertThat(AwaitilityWrapper.createDefault().until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoint.MODULES_PAGE)))).isTrue();
    }
}
