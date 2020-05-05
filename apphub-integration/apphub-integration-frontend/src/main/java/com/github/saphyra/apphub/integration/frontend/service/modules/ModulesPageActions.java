package com.github.saphyra.apphub.integration.frontend.service.modules;

import com.github.saphyra.apphub.integration.common.framework.Endpoint;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class ModulesPageActions {
    public static void logout(WebDriver driver) {
        assertThat(driver.getCurrentUrl()).isEqualTo(UrlFactory.create(Endpoint.MODULES_PAGE));

        ModulesPage.logoutButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(Endpoint.WEB_ROOT)));

        NotificationUtil.verifySuccessNotification(driver, "Sikeres kijelentkezés.");
    }
}
