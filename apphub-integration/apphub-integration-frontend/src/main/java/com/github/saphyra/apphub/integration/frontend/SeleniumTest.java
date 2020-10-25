package com.github.saphyra.apphub.integration.frontend;

import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.frontend.framework.SleepUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@Slf4j
public class SeleniumTest extends TestBase {
    static final boolean HEADLESS_MODE;

    static {
        String arg = System.getProperty("headless");
        boolean headless;
        if (isNull(arg)) {
            headless = false;
        } else {
            headless = Boolean.parseBoolean(arg);
        }
        HEADLESS_MODE = headless;
    }

    private final ThreadLocal<WebDriverWrapper> driverWrapper = new ThreadLocal<>();

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult testResult) {
        WebDriverWrapper webDriverWrapper = driverWrapper.get();
        if (isNull(webDriverWrapper)) {
            return;
        }
        WebDriver driver = webDriverWrapper.getDriver();
        if (ITestResult.FAILURE == testResult.getStatus() && !HEADLESS_MODE) {
            extractLogs(driver);
            SleepUtil.sleep(20000);
        }
        WebDriverFactory.release(webDriverWrapper.getId());
    }

    @AfterSuite
    public void stopDrivers() {
        WebDriverFactory.stopDrivers();
    }

    protected WebDriver extractDriver() {
        StopWatch stopWatch = StopWatch.createStarted();
        Future<WebDriverWrapper> webDriverWrapperFuture = WebDriverFactory.getDriver();

        while (!webDriverWrapperFuture.isDone()) {
            SleepUtil.sleep(1000);
        }

        stopWatch.stop();
        log.info("WebDriver allocated in {} seconds", stopWatch.getTime(TimeUnit.SECONDS));

        WebDriverWrapper webDriverWrapper;
        try {
            webDriverWrapper = webDriverWrapperFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        driverWrapper.set(webDriverWrapper);

        WebDriver driver = webDriverWrapper.getDriver();
        driver.manage().deleteAllCookies();

        return driver;
    }

    private void extractLogs(WebDriver driver) {
        log.info("Extracting logs...");
        driver.findElements(By.cssSelector("#logcontainermessages > div"))
            .stream()
            .map(this::extractMessage)
            .forEach(logMessage -> log.info(logMessage.toString()));
        log.info("Logs extracted.");
    }

    private LogMessage extractMessage(WebElement webElement) {
        return LogMessage.builder()
            .severity(webElement.findElement(By.cssSelector(":first-child")).getText())
            .title(webElement.findElement(By.cssSelector(":nth-child(2)")).getText())
            .message(webElement.findElement(By.cssSelector(":nth-child(3)")).getText())
            .build();
    }

    @Data
    @Builder
    private static class LogMessage {
        private final String severity;
        private final String title;
        private final String message;

        @Override
        public String toString() {
            return String.format("%s --- %s - %s", severity, title, message);
        }
    }
}
