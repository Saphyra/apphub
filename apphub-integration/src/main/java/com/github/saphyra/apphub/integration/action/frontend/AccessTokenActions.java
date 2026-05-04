package com.github.saphyra.apphub.integration.action.frontend;

import com.github.saphyra.apphub.integration.framework.UrlFactory;
import org.openqa.selenium.WebDriver;

public class AccessTokenActions {
    public static void invalidateAccessToken(WebDriver driver, int serverPort) {
        driver.navigate()
            .to(UrlFactory.create(serverPort, "/invalidate-access-token/web"));
    }
}
