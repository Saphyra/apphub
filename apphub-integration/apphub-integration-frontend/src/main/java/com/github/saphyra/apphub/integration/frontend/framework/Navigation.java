package com.github.saphyra.apphub.integration.frontend.framework;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import org.openqa.selenium.WebDriver;

public class Navigation {
    public static String toIndexPage(WebDriver driver) {
        String url = UrlFactory.create(Endpoints.INDEX_PAGE);
        driver.navigate().to(url);
        return url;
    }

    public static void toIndexPage(WebDriver driver, boolean shouldWait) {
        String url = toIndexPage(driver);

        if (shouldWait) {
            AwaitilityWrapper.createDefault()
                .until(() -> driver.getCurrentUrl().equals(url));
        }
    }
}
