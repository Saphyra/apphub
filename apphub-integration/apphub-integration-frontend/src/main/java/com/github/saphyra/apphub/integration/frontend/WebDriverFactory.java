package com.github.saphyra.apphub.integration.frontend;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.saphyra.apphub.integration.frontend.SeleniumTest.HEADLESS_MODE;
import static java.util.Objects.isNull;

@Slf4j
class WebDriverFactory implements PooledObjectFactory<UUID> {
    private static final int BROWSER_STARTUP_LIMIT = 2;
    private static final int MAX_DRIVER_COUNT = 15;

    private static final GenericObjectPoolConfig<UUID> DRIVER_POOL_CONFIG = new GenericObjectPoolConfig<>();

    static {
        DRIVER_POOL_CONFIG.setMaxTotal(MAX_DRIVER_COUNT);
        DRIVER_POOL_CONFIG.setMinIdle(MAX_DRIVER_COUNT);
    }

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(BROWSER_STARTUP_LIMIT);
    private static final Map<UUID, WebDriverWrapper> DRIVER_CACHE = new ConcurrentHashMap<>();
    private static final GenericObjectPool<UUID> DRIVER_POOL = new GenericObjectPool<>(new WebDriverFactory(), DRIVER_POOL_CONFIG);

    public static void startDrivers() throws Exception {
        DRIVER_POOL.addObjects(MAX_DRIVER_COUNT);
    }

    public static WebDriverWrapper getDriver() throws Exception {
        return DRIVER_CACHE.get(DRIVER_POOL.borrowObject());
    }

    @NotNull
    private Future<WebDriverWrapper> createNewDriver() {
        WebDriverWrapper webDriverWrapper = new WebDriverWrapper();
        DRIVER_CACHE.put(webDriverWrapper.getId(), webDriverWrapper);
        return EXECUTOR.submit(() -> {
            WebDriver webDriver = createDriver();
            webDriverWrapper.setDriver(webDriver);
            return webDriverWrapper;
        });
    }

    public static void release(WebDriverWrapper webDriverWrapper) {
        log.info("Releasing webDriver with id {}", webDriverWrapper.getId());
        WebDriver driver = webDriverWrapper.getDriver();
        try {
            driver.navigate().to(UrlFactory.create(Endpoints.ERROR_PAGE));

            AwaitilityWrapper.createDefault()
                .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ERROR_PAGE))
                .assertTrue("Failed to redirect to Error page. Current url: " + driver.getCurrentUrl());
            driver.manage().deleteAllCookies();
        } catch (Exception e) {
            log.error("Failed releasing driver. Removing from cache...");
            try {
                DRIVER_POOL.invalidateObject(webDriverWrapper.getId());
                return;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        DRIVER_POOL.returnObject(webDriverWrapper.getId());
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
                log.info("Driver created: {}", driver);
                return driver;
            } catch (Exception e) {
                log.error("Could not create driver", e);
            }
        }

        throw new RuntimeException("Failed to create webDriver");
    }

    static void stopDrivers() {
        DRIVER_POOL.close();
    }

    private static void stopDriver(WebDriverWrapper webDriverWrapper) {
        WebDriver driver = webDriverWrapper.getDriver();
        for (int tryCount = 0; tryCount < 3; tryCount++) {
            try {
                driver.close();
                driver.quit();
                break;
            } catch (Exception e) {
                log.error("Could not stop driver", e);
            }
        }
    }

    @Override
    public PooledObject<UUID> makeObject() throws Exception {
        Future<WebDriverWrapper> newDriver = createNewDriver();
        while (!newDriver.isDone()) {
            SleepUtil.sleep(1000);
        }
        WebDriverWrapper webDriverWrapper = newDriver.get();

        DRIVER_CACHE.put(webDriverWrapper.getId(), webDriverWrapper);

        return new DefaultPooledObject<>(newDriver.get().getId());
    }

    @Override
    public void destroyObject(PooledObject<UUID> p) {
        stopDriver(DRIVER_CACHE.get(p.getObject()));
    }

    @Override
    public void destroyObject(PooledObject<UUID> p, DestroyMode mode) {
        destroyObject(p);
    }

    @Override
    public boolean validateObject(PooledObject<UUID> p) {
        return !DRIVER_CACHE.get(p.getObject())
            .isLocked();
    }

    @Override
    public void activateObject(PooledObject<UUID> p) {
        DRIVER_CACHE.get(p.getObject())
            .setLocked(true);
    }

    @Override
    public void passivateObject(PooledObject<UUID> p) {
        DRIVER_CACHE.get(p.getObject())
            .setLocked(false);
    }
}
