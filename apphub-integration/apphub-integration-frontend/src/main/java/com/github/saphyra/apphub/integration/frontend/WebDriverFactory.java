package com.github.saphyra.apphub.integration.frontend;

import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.SleepUtil;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.saphyra.apphub.integration.frontend.SeleniumTest.HEADLESS_MODE;
import static java.util.Objects.isNull;

@Slf4j
class WebDriverFactory implements PooledObjectFactory<WebDriverWrapper> {
    private static final int BROWSER_STARTUP_LIMIT = 3;
    private static final int MAX_DRIVER_COUNT = 50;
    private static final int PRE_CREATE_COUNT = 15;

    private static final GenericObjectPoolConfig<WebDriverWrapper> DRIVER_POOL_CONFIG = new GenericObjectPoolConfig<>();


    static {
        DRIVER_POOL_CONFIG.setMaxTotal(MAX_DRIVER_COUNT);
        DRIVER_POOL_CONFIG.setMinIdle(MAX_DRIVER_COUNT);
    }

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(BROWSER_STARTUP_LIMIT);
    private static final GenericObjectPool<WebDriverWrapper> DRIVER_POOL = new GenericObjectPool<>(new WebDriverFactory(), DRIVER_POOL_CONFIG);

    public static void startDrivers() throws Exception {
        DRIVER_POOL.addObjects(PRE_CREATE_COUNT);
    }

    public static WebDriverWrapper getDriver() throws Exception {
        return DRIVER_POOL.borrowObject();
    }

    @NotNull
    private WebDriverWrapper createNewDriver() {
        Future<WebDriver> future = EXECUTOR.submit(this::createDriver);
        return new WebDriverWrapper(future);
    }

    public static void release(WebDriverWrapper webDriverWrapper) {
        log.info("Releasing webDriver with id {}", webDriverWrapper.getId());
        try {
            WebDriver driver = webDriverWrapper.getDriver();
            driver.navigate().to(UrlFactory.create(Endpoints.ERROR_PAGE));

            AwaitilityWrapper.createDefault()
                .until(() -> driver.getCurrentUrl().endsWith(Endpoints.ERROR_PAGE))
                .assertTrue("Failed to redirect to Error page. Current url: " + driver.getCurrentUrl());
            driver.manage().deleteAllCookies();
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
                SleepUtil.sleep(1000);
                driver.navigate().to(UrlFactory.create(Endpoints.ERROR_PAGE));
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
        log.info("Driver {} is stopped.", webDriverWrapper.getId());
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
