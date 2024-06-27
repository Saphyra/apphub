package com.github.saphyra.apphub.integration.framework;

import org.openqa.selenium.WebDriver;

public class Navigation {
    public static String toIndexPage(WebDriver driver) {
        String url = UrlFactory.create(Endpoints.INDEX_PAGE);
        toUrl(driver, url);
        return url;
    }

    public static void toIndexPage(WebDriver driver, boolean shouldWait) {
        String url = toIndexPage(driver);

        if (shouldWait) {
            AwaitilityWrapper.createDefault()
                .until(() -> driver.getCurrentUrl().equals(url));
        }
    }

    public synchronized static void toUrl(WebDriver driver, String url) {
        driver.navigate().to(url);
    }
}
