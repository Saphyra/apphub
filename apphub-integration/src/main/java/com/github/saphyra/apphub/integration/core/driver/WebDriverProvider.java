package com.github.saphyra.apphub.integration.core.driver;

import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.core.connection.ConnectionProvider;
import com.github.saphyra.apphub.integration.core.util.CacheItemWrapper;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.concurrent.FutureWrapper;
import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.WebStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.core.driver.WebDriverFactory.numberOfDriversCreated;

@Slf4j
public class WebDriverProvider {
    private static final GenericObjectPool<WebDriverWrapper> DEFAULT_DRIVER_POOL = new GenericObjectPool<>(new WebDriverFactory(WebDriverMode.DEFAULT), defaultDriverPoolConfig());
    private static final GenericObjectPool<WebDriverWrapper> HEADED_DRIVER_POOL = new GenericObjectPool<>(new WebDriverFactory(WebDriverMode.HEADED), headedDriverPoolConfig());

    private static final int MAX_DRIVER_COUNT = 300;
    public static final Map<WebDriverMode, GenericObjectPool<WebDriverWrapper>> CACHE_MAP = Map.of(
        WebDriverMode.DEFAULT, DEFAULT_DRIVER_POOL,
        WebDriverMode.HEADED, HEADED_DRIVER_POOL
    );

    private static GenericObjectPoolConfig<WebDriverWrapper> defaultDriverPoolConfig() {
        GenericObjectPoolConfig<WebDriverWrapper> config = new GenericObjectPoolConfig<>();

        config.setMaxTotal(MAX_DRIVER_COUNT);
        config.setMaxIdle(MAX_DRIVER_COUNT);
        config.setTestOnBorrow(true);

        return config;
    }

    private static GenericObjectPoolConfig<WebDriverWrapper> headedDriverPoolConfig() {
        GenericObjectPoolConfig<WebDriverWrapper> config = new GenericObjectPoolConfig<>();

        config.setMaxTotal(1);
        config.setMaxIdle(1);
        config.setTestOnBorrow(true);

        return config;
    }

    public static synchronized List<WebDriverWrapper> getDrivers(int driverCount) {
        return getDrivers(driverCount, WebDriverMode.DEFAULT);
    }

    public static synchronized List<WebDriverWrapper> getDrivers(int driverCount, WebDriverMode mode) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<FutureWrapper<WebDriverWrapper>> futures = Stream.generate(() -> TestBase.EXECUTOR_SERVICE.asyncProcess(() -> getDriver(mode)))
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
    public static void invalidate(WebDriverWrapper webDriverWrapper) {
        if (TestConfiguration.WEB_DRIVER_CACHE_ENABLED) {
            getCacheForMode(webDriverWrapper.getMode())
                .invalidateObject(webDriverWrapper, DestroyMode.ABANDONED);
        }
        WebDriverFactory.stopDriver(webDriverWrapper);
    }

    public static void release(WebDriverWrapper webDriverWrapper) {
        if (TestConfiguration.WEB_DRIVER_CACHE_ENABLED) {
            returnDriverToCache(webDriverWrapper);
        } else {
            WebDriverFactory.stopDriver(webDriverWrapper);
        }
    }

    public static void stopDrivers() {
        DEFAULT_DRIVER_POOL.close();
        HEADED_DRIVER_POOL.close();
        log.info("NumberOfDriversCreated: {}", numberOfDriversCreated);
    }

    @SneakyThrows
    public static WebDriverWrapper getDriver(WebDriverMode mode) {
        if (TestConfiguration.WEB_DRIVER_CACHE_ENABLED) {
            return getCacheForMode(mode)
                .borrowObject();
        } else {
            return WebDriverFactory.createNewDriver(mode);
        }
    }

    private static GenericObjectPool<WebDriverWrapper> getCacheForMode(WebDriverMode mode) {
        return Optional.ofNullable(CACHE_MAP.get(mode))
            .orElseThrow(() -> new IllegalArgumentException("Unsupported WebDriverMode " + mode));
    }

    private static void returnDriverToCache(WebDriverWrapper webDriverWrapper) {
        log.debug("Releasing webDriver with id {}", webDriverWrapper.getId());

        CacheItemWrapper<Integer> serverPort = ConnectionProvider.getServerPort();

        try {
            WebDriver driver = webDriverWrapper.getDriver();

            List<String> handles = new ArrayList<>(driver.getWindowHandles());
            for (int i = handles.size() - 1; i > 0; i--) {
                driver.switchTo()
                    .window(handles.get(i));
                driver.close();
            }

            driver.switchTo().window(handles.get(0));
            driver.navigate().to(UrlFactory.create(serverPort.getItem(), Endpoints.ERROR_PAGE));

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
                DEFAULT_DRIVER_POOL.invalidateObject(webDriverWrapper);
                return;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        } finally {
            ConnectionProvider.releaseServerPort(serverPort);
        }

        getCacheForMode(webDriverWrapper.getMode())
            .returnObject(webDriverWrapper);
    }
}
