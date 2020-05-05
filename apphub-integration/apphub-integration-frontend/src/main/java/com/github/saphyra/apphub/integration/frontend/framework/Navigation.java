package com.github.saphyra.apphub.integration.frontend.framework;

import com.github.saphyra.apphub.integration.common.framework.Endpoint;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import org.openqa.selenium.WebDriver;

public class Navigation {
    public static void toIndexPage(WebDriver driver) {
        String url = UrlFactory.create(Endpoint.WEB_ROOT);

        driver.navigate().to(url);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(url));
    }
}
