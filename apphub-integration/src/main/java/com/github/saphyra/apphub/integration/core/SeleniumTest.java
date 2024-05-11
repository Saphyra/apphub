package com.github.saphyra.apphub.integration.core;

import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
public abstract class SeleniumTest extends TestBase {
    private static final ThreadLocal<List<WebDriverWrapper>> driverWrappers = new ThreadLocal<>();

    @AfterMethod(alwaysRun = true)
    public synchronized void afterMethod(ITestResult testResult) {
        try {
            List<WebDriverWrapper> wrappers = driverWrappers.get();
            for (int driverIndex = 0; driverIndex < wrappers.size(); driverIndex++) {
                WebDriverWrapper webDriverWrapper = wrappers.get(driverIndex);
                WebDriver driver = webDriverWrapper.getDriver();
                if (ITestResult.FAILURE == testResult.getStatus()) {
                    log.debug("Current URL: {}", driver.getCurrentUrl());
                    reportFailure(webDriverWrapper, testResult.getTestClass().getRealClass().getName(), testResult.getName(), driverIndex);
                    WebDriverFactory.invalidate(webDriverWrapper);
                } else {
                    WebDriverFactory.release(webDriverWrapper);
                }
            }
        } finally {
            driverWrappers.remove();
        }
    }

    @SneakyThrows
    private void reportFailure(WebDriverWrapper driver, String className, String method, int driverIndex) {
        String directory = getReportDirectory(className, method);
        takeScreenshot(driver, directory, driverIndex);
        saveLogs(driver, directory, driverIndex);
    }

    private static void takeScreenshot(WebDriverWrapper driver, String directory, int driverIndex) throws IOException {
        String fileName = directory + String.format("/screenshot_%s.png", driverIndex);
        log.debug("Screenshot fileName: {}", fileName);

        TakesScreenshot scrShot = ((TakesScreenshot) driver.getDriver());
        File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File target = new File(fileName);
        FileUtils.copyFile(srcFile, target);
    }

    private static void saveLogs(WebDriverWrapper driver, String directory, int driverIndex) throws IOException {
        List<String> entries = new ArrayList<>();
        LogEntries logEntries = driver.getDriver().manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            String logEntry = entry.getMessage();
            log.debug(logEntry);
            entries.add(logEntry);
        }

        String fileContent = new GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(entries);
        log.debug(fileContent);
        String fileName = directory + String.format("/console_log_%s.json", driverIndex);
        log.debug("Console log fileName: {}", fileName);

        Path path = Paths.get(fileName);
        File file = new File(fileName);
        file.createNewFile();
        Files.writeString(path, fileContent);
    }

    protected static WebDriver extractDriver() {
        return extractDrivers(1)
            .get(0);
    }

    public static WebDriver extractDriver(WebDriverMode mode) {
        if (mode == WebDriverMode.DEFAULT) {
            throw new IllegalArgumentException("Use extractDriver() to extract default driver.");
        }
        WebDriverWrapper webDriverWrapper = new WebDriverWrapper(WebDriverFactory.createDriverExternal(mode), mode);
        addToDrivers(webDriverWrapper);
        return webDriverWrapper.getDriver();
    }

    private static void addToDrivers(WebDriverWrapper webDriverWrapper) {
        if (isNull(driverWrappers.get())) {
            driverWrappers.set(new ArrayList<>());
        }

        driverWrappers.get()
            .add(webDriverWrapper);
    }

    protected static List<WebDriver> extractDrivers(int driverCount) {
        StopWatch stopWatch = StopWatch.createStarted();
        List<WebDriverWrapper> webDriverWrappers;
        try {
            webDriverWrappers = WebDriverFactory.getDrivers(driverCount);
            stopWatch.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.debug("{} WebDriver(s) allocated in {} seconds", driverCount, stopWatch.getTime(TimeUnit.SECONDS));

        if (isNull(driverWrappers.get())) {
            driverWrappers.set(new ArrayList<>());
        }
        driverWrappers.get().addAll(webDriverWrappers);

        return webDriverWrappers.stream()
            .map(WebDriverWrapper::getDriver)
            .collect(Collectors.toList());
    }

    @Override
    protected String getTestType() {
        return "fe";
    }
}
