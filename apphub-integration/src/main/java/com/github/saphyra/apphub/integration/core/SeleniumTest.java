package com.github.saphyra.apphub.integration.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@Slf4j
public class SeleniumTest extends TestBase {
    static final boolean HEADLESS_MODE;

    static {
        HEADLESS_MODE = Boolean.parseBoolean(System.getProperty("headless"));
    }

    private static final ThreadLocal<List<WebDriverWrapper>> driverWrappers = new ThreadLocal<>();

    @AfterMethod(alwaysRun = true)
    public synchronized void afterMethod(ITestResult testResult) {
        driverWrappers.get()
            .forEach(webDriverWrapper -> {
                WebDriver driver = webDriverWrapper.getDriver();
                if (ITestResult.FAILURE == testResult.getStatus()) {
                    log.error("Current URL: {}", driver.getCurrentUrl());
                    takeScreenshot(webDriverWrapper, testResult.getName());
                }
                WebDriverFactory.release(webDriverWrapper);
            });
        driverWrappers.remove();
    }

    private void takeScreenshot(WebDriverWrapper driver, String method) {
        TakesScreenshot scrShot = ((TakesScreenshot) driver.getDriver());
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File target = new File("d:/screenshots/" + method + "/" + driver.getId() + ".png");
        try {
            FileUtils.copyFile(SrcFile, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static WebDriver extractDriver() {
        StopWatch stopWatch = StopWatch.createStarted();
        WebDriverWrapper webDriverWrapper;
        try {
            webDriverWrapper = WebDriverFactory.getDriver();
            stopWatch.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.debug("WebDriver with id {} allocated in {} seconds", webDriverWrapper.getId(), stopWatch.getTime(TimeUnit.SECONDS));

        if (isNull(driverWrappers.get())) {
            driverWrappers.set(new ArrayList<>());
        }
        driverWrappers.get().add(webDriverWrapper);

        WebDriver driver = webDriverWrapper.getDriver();
        driver.manage().deleteAllCookies();

        return driver;
    }
}
