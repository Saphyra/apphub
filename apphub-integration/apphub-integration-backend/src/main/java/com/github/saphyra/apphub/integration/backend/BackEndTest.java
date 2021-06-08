package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BackEndTest extends TestBase {
    public static final int AVAILABLE_PERMITS = 15;
    private static final Semaphore SEMAPHORE = new Semaphore(AVAILABLE_PERMITS);

    @DataProvider(name = "languageDataProvider")
    public Object[] languageDataProvider() {
        return Language.values();
    }

    @BeforeMethod(alwaysRun = true)
    public void waitForPermit() {
        try {
            log.debug("Available permits before acquiring: {}", SEMAPHORE.availablePermits());
            Stopwatch stopwatch = Stopwatch.createStarted();
            SEMAPHORE.acquire(getNecessaryPermitCount());
            stopwatch.stop();
            log.info("Permit acquired in {}ms. Permits left: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), SEMAPHORE.availablePermits());
        } catch (InterruptedException e) {
            log.error("Thread interrupted.", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpWsConnections() {
        try {
            ApphubWsClient.cleanUpConnections();
        } finally {
            log.debug("Available permits before releasing: {}", SEMAPHORE.availablePermits());
            SEMAPHORE.release(getNecessaryPermitCount());
            log.debug("Available permits after releasing: {}", SEMAPHORE.availablePermits());
        }
    }

    public int getNecessaryPermitCount() {
        return 1;
    }
}
