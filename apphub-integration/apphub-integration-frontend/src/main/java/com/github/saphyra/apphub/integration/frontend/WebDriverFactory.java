package com.github.saphyra.apphub.integration.frontend;

import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.saphyra.apphub.integration.frontend.SeleniumTest.HEADLESS_MODE;
import static java.util.Objects.isNull;

@Slf4j
class WebDriverFactory {
    public static final int BROWSER_STARTUP_LIMIT = 3;
    private static final int MAX_DRIVER_COUNT = 15;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(BROWSER_STARTUP_LIMIT);

    private static final Map<UUID, WebDriverWrapper> DRIVER_CACHE = new ConcurrentHashMap<>();

    static synchronized Future<WebDriverWrapper> getDriver() {
        log.info("Querying driver...");
        for (int i = 0; true; i++) {
            Optional<WebDriverWrapper> webDriverWrapperOptional = DRIVER_CACHE.values()
                .stream()
                .filter(wrapper -> !wrapper.isLocked())
                .findAny();

            if (webDriverWrapperOptional.isPresent()) {
                WebDriverWrapper webDriverWrapper = webDriverWrapperOptional.get();
                webDriverWrapper.setLocked(true);
                return EXECUTOR.submit(() -> webDriverWrapper);
            } else if (DRIVER_CACHE.size() < MAX_DRIVER_COUNT) {
                log.info("No available webDriver found. Creating a new one...");
                WebDriverWrapper webDriverWrapper = new WebDriverWrapper();
                DRIVER_CACHE.put(webDriverWrapper.getId(), webDriverWrapper);
                return EXECUTOR.submit(() -> {
                    WebDriver webDriver = createDriver();
                    webDriverWrapper.setDriver(webDriver);
                    return webDriverWrapper;
                });
            }

            log.debug("No available webDriver found and limit is reached. Waiting for releasing for {} seconds", i);
            SleepUtil.sleep(1000);
        }
    }

    public static void release(UUID id) {
        log.info("Releasing webDriver with id {}", id);
        WebDriverWrapper webDriverWrapper = DRIVER_CACHE.get(id);
        WebDriver driver = webDriverWrapper.getDriver();
        driver.manage().deleteAllCookies();
        driver.navigate().to(UrlFactory.create(Endpoints.INDEX_PAGE));
        webDriverWrapper.setLocked(false);
    }

    private static WebDriver createDriver() {
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
        DRIVER_CACHE.entrySet()
            .stream()
            .parallel()
            .forEach(WebDriverFactory::stopDriver);
    }

    private static void stopDriver(Map.Entry<UUID, WebDriverWrapper> entry) {
        stopDriver(entry.getKey(), entry.getValue());
    }

    private static void stopDriver(UUID uuid, WebDriverWrapper webDriverWrapper) {
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
        DRIVER_CACHE.remove(uuid);
    }
}
