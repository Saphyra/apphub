package com.github.saphyra.apphub.integration.action.frontend.account;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AccountPageActions {
    public static void back(int serverPort, WebDriver driver) {
        driver.findElement(By.id("account-home-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().equals(UrlFactory.create(serverPort, Endpoints.MODULES_PAGE)))
            .assertTrue();
    }
}
