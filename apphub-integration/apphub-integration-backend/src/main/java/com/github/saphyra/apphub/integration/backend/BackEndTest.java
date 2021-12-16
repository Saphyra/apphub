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

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BackEndTest extends TestBase {
    private static final int AVAILABLE_PERMITS = 15;
    private static final Semaphore SEMAPHORE = new Semaphore(AVAILABLE_PERMITS);

    @DataProvider(name = "languageDataProvider", parallel = true)
    public Object[] languageDataProvider() {
        //return Language.values();
        return new Object[]{Language.HUNGARIAN};
    }

    @BeforeMethod(alwaysRun = true)
    public void waitForPermit(Method method) throws InterruptedException {

        log.debug("Available permits before acquiring: {}", SEMAPHORE.availablePermits());
        Stopwatch stopwatch = Stopwatch.createStarted();
        acquirePermit(method, stopwatch);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup(Method method) {

        String methodName = method.getName();

        boolean wsConnectionsCleaned = ApphubWsClient.getClients().isEmpty();
        if (!wsConnectionsCleaned) {
            log.error("WsConnections not cleaned for method {}", method.getName(), new RuntimeException());
            ApphubWsClient.cleanUpConnections();
        }
        assertThat(ApphubWsClient.getClients().isEmpty()).isTrue();

        log.debug("Available permits before releasing: {}", SEMAPHORE.availablePermits());
        SEMAPHORE.release(1);
        log.info("Available permits after releasing of {}: {}", methodName, SEMAPHORE.availablePermits());
    }

    private static synchronized void acquirePermit(Method method, Stopwatch stopwatch) throws InterruptedException {
        SEMAPHORE.acquire(1);
        stopwatch.stop();
        log.info("Permit acquired for test {} in {}ms. Permits left: {}", method.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS), SEMAPHORE.availablePermits());
    }
}
