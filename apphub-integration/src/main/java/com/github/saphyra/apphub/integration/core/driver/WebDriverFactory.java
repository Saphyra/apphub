package com.github.saphyra.apphub.integration.core.driver;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.core.connection.ConnectionProvider;
import com.github.saphyra.apphub.integration.core.util.CacheItemWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.GenericEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
class WebDriverFactory implements PooledObjectFactory<WebDriverWrapper> {
    static final ExecutorService BROWSER_STARTUP_EXECUTOR = Executors.newFixedThreadPool(TestConfiguration.BROWSER_STARTUP_LIMIT);

    public static volatile int numberOfDriversCreated = 0;
    private final WebDriverMode mode;

    @Override
    public PooledObject<WebDriverWrapper> makeObject() {
        WebDriverWrapper newDriver = createNewDriver(mode);
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
            .contains(GenericEndpoints.ERROR_PAGE);
    }

    @Override
    public void activateObject(PooledObject<WebDriverWrapper> p) {
    }

    @Override
    public void passivateObject(PooledObject<WebDriverWrapper> p) {
    }

    static WebDriverWrapper createNewDriver(WebDriverMode mode) {
        Future<WebDriver> future = BROWSER_STARTUP_EXECUTOR.submit(() -> createDriver(mode));
        return new WebDriverWrapper(future, mode);
    }

    static void stopDriver(WebDriverWrapper webDriverWrapper) {
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
    }

    private static ChromeDriver createDriver(WebDriverMode mode) {
        log.info("Creating WebDriver with mode {}", mode);
        CacheItemWrapper<Integer> serverPort = ConnectionProvider.getServerPort();
        try {
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
                    Navigation.toUrl(driver, UrlFactory.create(serverPort.getItem(), GenericEndpoints.ERROR_PAGE));
                    numberOfDriversCreated++;
                    return driver;
                } catch (Exception e) {
                    log.error("Could not create driver", e);
                }
            }

            if (nonNull(driver)) {
                driver.close();
            }
            throw new RuntimeException("Failed to create webDriver");
        } finally {
            ConnectionProvider.releaseServerPort(serverPort);
        }
    }
}
