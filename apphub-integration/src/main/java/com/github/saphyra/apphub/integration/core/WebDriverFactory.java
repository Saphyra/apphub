package com.github.saphyra.apphub.integration.core;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.google.common.base.Stopwatch;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.html5.WebStorage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.core.SeleniumTest.HEADLESS_MODE;
import static java.util.Objects.isNull;

@Slf4j
class WebDriverFactory implements PooledObjectFactory<WebDriverWrapper> {
    private static final int BROWSER_STARTUP_LIMIT = 3;
    private static final int MAX_DRIVER_COUNT = 30;

    private static final GenericObjectPoolConfig<WebDriverWrapper> DRIVER_POOL_CONFIG = new GenericObjectPoolConfig<>();

    private static volatile int numberOfDriversCreated = 0;

    static {
        DRIVER_POOL_CONFIG.setMaxTotal(MAX_DRIVER_COUNT);
    }

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(BROWSER_STARTUP_LIMIT);
    private static final GenericObjectPool<WebDriverWrapper> DRIVER_POOL = new GenericObjectPool<>(new WebDriverFactory(), DRIVER_POOL_CONFIG);

    static synchronized List<WebDriverWrapper> getDrivers(int driverCount) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<WebDriverWrapper> result = Stream.generate(WebDriverFactory::getDriver)
            .limit(driverCount)
            .collect(Collectors.toList());
        stopwatch.stop();
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (elapsed > 100) {
            log.debug("{} WebDrivers fetched in {}ms", driverCount, elapsed);
        }
        return result;
    }

    @SneakyThrows
    private static WebDriverWrapper getDriver() {
        return DRIVER_POOL.borrowObject();
    }

    private WebDriverWrapper createNewDriver() {
        Future<WebDriver> future = EXECUTOR.submit(this::createDriver);
        return new WebDriverWrapper(future);
    }

    static void release(WebDriverWrapper webDriverWrapper) {
        log.debug("Releasing webDriver with id {}", webDriverWrapper.getId());
        try {
            WebDriver driver = webDriverWrapper.getDriver();
            driver.navigate().to(UrlFactory.create(Endpoints.ERROR_PAGE));

            AwaitilityWrapper.createDefault()
                .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ERROR_PAGE))
                .assertTrue("Failed to redirect to Error page. Current url: " + driver.getCurrentUrl());
            driver.manage()
                .deleteAllCookies();
            ((WebStorage) driver).getSessionStorage()
                .clear();
        } catch (Exception e) {
            log.error("Failed releasing driver. Removing from cache...");
            try {
                DRIVER_POOL.invalidateObject(webDriverWrapper);
                return;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        DRIVER_POOL.returnObject(webDriverWrapper);
    }

    @SneakyThrows
    static void invalidate(WebDriverWrapper webDriverWrapper) {
        DRIVER_POOL.invalidateObject(webDriverWrapper, DestroyMode.ABANDONED);
        stopDriver(webDriverWrapper);
    }

    private WebDriver createDriver() {
        ChromeDriver driver = null;
        for (int tryCount = 0; tryCount < 3 && isNull(driver); tryCount++) {
            try {
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();
                options.setHeadless(HEADLESS_MODE);
                options.addArguments("window-size=1920,1080");

                driver = new ChromeDriver(options);
                log.debug("Driver created: {}", driver);
                SleepUtil.sleep(1000);
                driver.navigate().to(UrlFactory.create(Endpoints.ERROR_PAGE));
                numberOfDriversCreated++;
                return driver;
            } catch (Exception e) {
                log.error("Could not create driver", e);
            }
        }

        throw new RuntimeException("Failed to create webDriver");
    }

    static void stopDrivers() {
        DRIVER_POOL.close();
        log.info("NumberOfDriversCreated: {}", numberOfDriversCreated);
    }

    private static void stopDriver(WebDriverWrapper webDriverWrapper) {
        WebDriver driver = webDriverWrapper.getDriver();
        for (int tryCount = 0; tryCount < 3; tryCount++) {
            try {
                driver.quit();
                break;
            } catch (Exception e) {
                log.error("Could not stop driver", e);
            }
        }
        log.debug("Driver {} is stopped. Drivers left: {}", webDriverWrapper.getId(), DRIVER_POOL.listAllObjects().size());
    }

    @Override
    public PooledObject<WebDriverWrapper> makeObject() {
        WebDriverWrapper newDriver = createNewDriver();

        return new DefaultPooledObject<>(newDriver);
    }

    @Override
    public void destroyObject(PooledObject<WebDriverWrapper> p) {
        stopDriver(p.getObject());
    }

    @Override
    public void destroyObject(PooledObject<WebDriverWrapper> p, DestroyMode mode) {
        destroyObject(p);
    }

    @Override
    public boolean validateObject(PooledObject<WebDriverWrapper> p) {
        return p.getObject()
            .getDriver()
            .getCurrentUrl()
            .contains(Endpoints.ERROR_PAGE);
    }

    @Override
    public void activateObject(PooledObject<WebDriverWrapper> p) {
    }

    @Override
    public void passivateObject(PooledObject<WebDriverWrapper> p) {
    }
}
