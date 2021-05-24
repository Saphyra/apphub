package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.util.concurrent.Semaphore;

@Slf4j
public class BackEndTest extends TestBase {
    public static final int AVAILABLE_PERMITS = 10;
    private static final Semaphore SEMAPHORE = new Semaphore(AVAILABLE_PERMITS);

    @DataProvider(name = "localeDataProvider")
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @BeforeMethod(alwaysRun = true)
    public void waitForPermit() {
        try {
            log.info("Available permits before acquiring: {}", SEMAPHORE.availablePermits());
            SEMAPHORE.acquire(getNecessaryPermitCount());
            log.info("Available permits after acquiring: {}", SEMAPHORE.availablePermits());
        } catch (InterruptedException e) {
            log.error("Thread interrupted.", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpWsConnections() {
        try {
            ApphubWsClient.cleanUpConnections();
        } finally {
            log.info("Available permits before releasing: {}", SEMAPHORE.availablePermits());
            SEMAPHORE.release(getNecessaryPermitCount());
            log.info("Available permits after releasing: {}", SEMAPHORE.availablePermits());
        }
    }

    public int getNecessaryPermitCount() {
        return 1;
    }
}
