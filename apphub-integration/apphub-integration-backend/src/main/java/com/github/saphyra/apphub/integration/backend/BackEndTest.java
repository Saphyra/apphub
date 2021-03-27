package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.backend.ws.AbstractWsClient;
import com.github.saphyra.apphub.integration.common.TestBase;
import org.testng.annotations.AfterMethod;

public class BackEndTest extends TestBase {
    @AfterMethod(alwaysRun = true)
    public void cleanUpWsConnections() {
        AbstractWsClient.cleanUpConnections();
    }
}
