package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

public class BackEndTest extends TestBase {
    @DataProvider(name = "localeDataProvider")
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpWsConnections() {
        ApphubWsClient.cleanUpConnections();
    }
}
