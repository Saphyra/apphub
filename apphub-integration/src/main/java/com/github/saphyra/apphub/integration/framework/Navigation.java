package com.github.saphyra.apphub.integration.framework;

import org.openqa.selenium.WebDriver;

public class Navigation {
    public static String toIndexPage(int serverPort, WebDriver driver) {
        String url = UrlFactory.create(serverPort, Endpoints.INDEX_PAGE);
        toUrl(driver, url);
        return url;
    }

    public static void toIndexPage(int serverPort, WebDriver driver, boolean shouldWait) {
        String url = toIndexPage(serverPort, driver);

        if (shouldWait) {
            AwaitilityWrapper.createDefault()
                .until(() -> driver.getCurrentUrl().equals(url));
        }
    }

    public synchronized static void toUrl(WebDriver driver, String url) {
        driver.navigate().to(url);
    }
}
