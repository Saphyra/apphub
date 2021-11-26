package com.github.saphyra.apphub.integration.backend;

import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BackEndTest extends TestBase {
    public static final int AVAILABLE_PERMITS = 15;
    private static final Semaphore SEMAPHORE = new Semaphore(AVAILABLE_PERMITS);

    @DataProvider(name = "languageDataProvider", parallel = true)
    public Object[] languageDataProvider() {
        return Language.values();
    }

    @BeforeMethod(alwaysRun = true)
    public void waitForPermit(Method method) {
        try {
            log.debug("Available permits before acquiring: {}", SEMAPHORE.availablePermits());
            Stopwatch stopwatch = Stopwatch.createStarted();
            acquirePermit(method, stopwatch);
        } catch (InterruptedException e) {
            log.error("Thread interrupted.", e);
        }
    }

    private static synchronized void acquirePermit(Method method, Stopwatch stopwatch) throws InterruptedException {
        SEMAPHORE.acquire(1);
        stopwatch.stop();
        log.info("Permit acquired for test {} in {}ms. Permits left: {}", method.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS), SEMAPHORE.availablePermits());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpWsConnections(Method method) {
        try {
            ApphubWsClient.cleanUpConnections();
        } finally {
            log.debug("Available permits before releasing: {}", SEMAPHORE.availablePermits());
            SEMAPHORE.release(1);
            log.info("Available permits after releasing of {}: {}", method.getName(), SEMAPHORE.availablePermits());
        }
    }
}
