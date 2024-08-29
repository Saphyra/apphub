package com.github.saphyra.apphub.integration.core.driver;

import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.core.connection.ConnectionProvider;
import com.github.saphyra.apphub.integration.core.util.CacheItemWrapper;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.google.common.base.Stopwatch;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Slf4j
public class WebDriverFactory implements PooledObjectFactory<WebDriverWrapper> {
    private static final int MAX_DRIVER_COUNT = 300;

    private static final GenericObjectPoolConfig<WebDriverWrapper> DRIVER_POOL_CONFIG = new GenericObjectPoolConfig<>();

    private static volatile int numberOfDriversCreated = 0;

    static {
        DRIVER_POOL_CONFIG.setMaxTotal(MAX_DRIVER_COUNT);
        DRIVER_POOL_CONFIG.setMaxIdle(MAX_DRIVER_COUNT);
        DRIVER_POOL_CONFIG.setTestOnBorrow(true);
    }

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(TestConfiguration.BROWSER_STARTUP_LIMIT);
    private static final GenericObjectPool<WebDriverWrapper> DRIVER_POOL = new GenericObjectPool<>(new WebDriverFactory(), DRIVER_POOL_CONFIG);

    public static synchronized List<WebDriverWrapper> getDrivers(int serverPort, int driverCount) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<FutureWrapper<WebDriverWrapper>> futures = Stream.generate(() -> TestBase.EXECUTOR_SERVICE.asyncProcess(() -> getDriver(serverPort)))
            .limit(driverCount)
            .toList();

        List<WebDriverWrapper> result = futures.stream()
            .map(webDriverWrapperFutureWrapper -> webDriverWrapperFutureWrapper.get().getOrThrow())
            .collect(Collectors.toList());
        stopwatch.stop();
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (elapsed > 100) {
            log.debug("{} WebDrivers fetched in {}ms", driverCount, elapsed);
        }
        return result;
    }

    @SneakyThrows
    private static WebDriverWrapper getDriver(int serverPort) {
        if (TestConfiguration.WEB_DRIVER_CACHE_ENABLED) {
            return DRIVER_POOL.borrowObject();
        } else {
            return createNewDriver(serverPort);
        }
    }

    private static WebDriverWrapper createNewDriver(int serverPort) {
        Future<WebDriver> future = EXECUTOR.submit(() -> createDriver(serverPort, WebDriverMode.DEFAULT));
        return new WebDriverWrapper(future, WebDriverMode.DEFAULT);
    }

    public static void release(int serverPort, WebDriverWrapper webDriverWrapper) {
        if (TestConfiguration.WEB_DRIVER_CACHE_ENABLED && webDriverWrapper.getMode() != WebDriverMode.HEADED) {
            returnDriverToCache(serverPort, webDriverWrapper);
        } else {
            stopDriver(webDriverWrapper);
        }
    }

    private static void returnDriverToCache(int serverPort, WebDriverWrapper webDriverWrapper) {
        log.debug("Releasing webDriver with id {}", webDriverWrapper.getId());
        try {
            WebDriver driver = webDriverWrapper.getDriver();

            List<String> handles = new ArrayList<>(driver.getWindowHandles());
            for (int i = handles.size() - 1; i > 0; i--) {
                driver.switchTo()
                    .window(handles.get(i));
                driver.close();
            }

            driver.switchTo().window(handles.get(0));
            driver.navigate().to(UrlFactory.create(serverPort, Endpoints.ERROR_PAGE));

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
    public static void invalidate(WebDriverWrapper webDriverWrapper) {
        if (TestConfiguration.WEB_DRIVER_CACHE_ENABLED && webDriverWrapper.getMode() == WebDriverMode.DEFAULT) {
            DRIVER_POOL.invalidateObject(webDriverWrapper, DestroyMode.ABANDONED);
        }
        stopDriver(webDriverWrapper);
    }

    public static Future<WebDriver> createDriverExternal(int serverPort, WebDriverMode mode) {
        return EXECUTOR.submit(() -> createDriver(serverPort, mode));
    }

    private static ChromeDriver createDriver(int serverPort, WebDriverMode mode) {
        log.info("Creating WebDriver with mode {}", mode);
        ChromeDriver driver = null;
        for (int tryCount = 0; tryCount < 3 && isNull(driver); tryCount++) {
            try {
                ChromeOptions options = new ChromeOptions();
                if (mode.getMode().get()) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--lang=hu");
                options.addArguments("window-size=1920,1080");
                options.addArguments("--disable-search-engine-choice-screen");

                driver = new ChromeDriver(options);
                log.debug("Driver created: {}", driver);
                SleepUtil.sleep(1000);
                Navigation.toUrl(driver, UrlFactory.create(serverPort, Endpoints.ERROR_PAGE));
                numberOfDriversCreated++;
                return driver;
            } catch (Exception e) {
                log.error("Could not create driver", e);
            }
        }

        throw new RuntimeException("Failed to create webDriver");
    }

    public static void stopDrivers() {
        DRIVER_POOL.close();
        log.info("NumberOfDriversCreated: {}", numberOfDriversCreated);
    }

    private static void stopDriver(WebDriverWrapper webDriverWrapper) {
        log.info("Stopping driver with mode {}", webDriverWrapper.getMode());

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
        CacheItemWrapper<Integer> port = ConnectionProvider.getServerPort();
        try {
            WebDriverWrapper newDriver = createNewDriver(port.getItem());
            return new DefaultPooledObject<>(newDriver);
        } finally {
            ConnectionProvider.releaseServerPort(port);
        }
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
