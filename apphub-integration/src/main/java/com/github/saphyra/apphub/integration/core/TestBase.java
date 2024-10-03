package com.github.saphyra.apphub.integration.core;

import com.github.saphyra.apphub.integration.core.connection.ConnectionProvider;
import com.github.saphyra.apphub.integration.core.driver.WebDriverProvider;
import com.github.saphyra.apphub.integration.core.exception.ExceptionConverter;
import com.github.saphyra.apphub.integration.core.integration_server.IntegrationServer;
import com.github.saphyra.apphub.integration.core.util.AutoCloseableImpl;
import com.github.saphyra.apphub.integration.core.util.CacheItemWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ObjectMapperWrapper;
import com.github.saphyra.apphub.integration.framework.concurrent.ExecutorServiceBean;
import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@Slf4j
@Listeners(SkipDisabledTestsInterceptor.class)
public abstract class TestBase {
    public static final ExecutorServiceBean EXECUTOR_SERVICE = new ExecutorServiceBean(Executors.newCachedThreadPool());
    public static final ObjectMapperWrapper OBJECT_MAPPER_WRAPPER = new ObjectMapperWrapper();

    private static final Semaphore SEMAPHORE = new Semaphore(TestConfiguration.AVAILABLE_PERMITS, true);

    private static final ThreadLocal<String> EMAIL_DOMAIN = new ThreadLocal<>();
    protected static final ThreadLocal<CacheItemWrapper<Integer>> SERVER_PORT_CACHED_ITEM = new ThreadLocal<>();
    private static final ThreadLocal<Stopwatch> DURATION_STOPWATCH = new ThreadLocal<>();
    private static final OffsetDateTime TEST_START_TIME = OffsetDateTime.now();

    public static String getEmailDomain() {
        return EMAIL_DOMAIN.get();
    }

    protected static int getServerPort() {
        return SERVER_PORT_CACHED_ITEM.get()
            .getItem();
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite(ITestContext context) {
        log.info("Thread count: {}", TestConfiguration.AVAILABLE_PERMITS);
        log.info("Disabled test groups: {}", TestConfiguration.DISABLED_TEST_GROUPS);
        log.info("Enabled test groups: {}", TestConfiguration.ENABLED_TEST_GROUPS);
        log.info("ServerPort: {}", TestConfiguration.SERVER_PORT);
        log.info("DatabasePort: {}", TestConfiguration.DATABASE_PORT);
        log.info("DatabaseName: {}", TestConfiguration.DATABASE_NAME);
        log.info("RestLoggingEnabled: {}", TestConfiguration.REST_LOGGING_ENABLED);
        log.info("IntegrationServerEnabled: {}", TestConfiguration.INTEGRATION_SERVER_ENABLED);
        log.info("IntegrationServerPort: {}", TestConfiguration.INTEGRATION_SERVER_PORT);
        log.info("Pre-create WebDrivers: {}", TestConfiguration.PRE_CREATE_WEB_DRIVERS);
        log.info("Server connection cache enabled: {}", TestConfiguration.SERVER_CONNECTION_CACHE_ENABLED);
        log.info("Database connection cache enabled: {}", TestConfiguration.DATABASE_CONNECTION_CACHE_ENABLED);
        log.info("Namespace name: {}", TestConfiguration.NAMESPACE_NAME);
        log.info("Browser startup limit: {}", TestConfiguration.BROWSER_STARTUP_LIMIT);

        IntegrationServer.start();
        StatusLogger.setTotalTestCount(context);

        //Checking if database is accessible
        // noinspection unused
        try (AutoCloseableImpl<Connection> conn = ConnectionProvider.getDatabaseConnection()) {
            log.info("Database connection successfully tested");
        } catch (Exception e) {
            log.error("Database check failed", e);
            System.exit(1);
        }

        try {
            CacheItemWrapper<Integer> serverPort = ConnectionProvider.getServerPort();
            ConnectionProvider.releaseServerPort(serverPort);
            log.info("API connection successfully tested");
        } catch (Exception e) {
            log.error("API check failed", e);
            System.exit(0);
        }

        for (ITestNGMethod method : context.getAllTestMethods()) {
            method.getXmlTest()
                .getSuite()
                .addListener(SkipDisabledTestsInterceptor.class.getName());

            if (Boolean.parseBoolean(System.getProperty("retryEnabled"))) {
                method.setRetryAnalyzerClass(RetryAnalyzerImpl.class);
            }
        }

        System.setProperty("testng.show.stack.frames", "true");
    }

    public static boolean isEnabled(ITestNGMethod method) {
        List<String> groups = Arrays.asList(method.getGroups());

        return TestConfiguration.DISABLED_TEST_GROUPS.stream()
            .noneMatch(groups::contains)
            && TestConfiguration.ENABLED_TEST_GROUPS.stream()
            .anyMatch(groups::contains);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite(ITestContext context) {
        WebDriverProvider.stopDrivers();
        ConnectionProvider.shutdownCaches();

        StatusLogger.logTestStartOrder();

        IntegrationServer.stop(context.getFailedTests().size() == 0 ? Constants.PASSED : Constants.FAILED);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws InterruptedException {
        TestGroupValidator.validateTestGroups(method);

        StatusLogger.removeFinishedTest(method);
        String testMethod = method.getDeclaringClass().getSimpleName() + "-" + method.getName();

        log.debug("Available permits before acquiring: {}", SEMAPHORE.availablePermits());
        Stopwatch stopwatch = Stopwatch.createStarted();
        acquirePermit(method, stopwatch);
        DURATION_STOPWATCH.set(Stopwatch.createStarted());

        EMAIL_DOMAIN.set(testMethod.toLowerCase() + "-" + UUID.randomUUID().toString().split("-")[0]);
        StatusLogger.addToStartOrder(method);

        CacheItemWrapper<Integer> serverPort = ConnectionProvider.getServerPort();
        SERVER_PORT_CACHED_ITEM.set(serverPort);
    }

    private static synchronized void acquirePermit(Method method, Stopwatch stopwatch) throws InterruptedException {
        SEMAPHORE.acquire(1);
        stopwatch.stop();
        log.debug("Permit acquired for test {} in {}ms. Permits left: {}", method.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS), SEMAPHORE.availablePermits());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(Method method, ITestResult testResult) {
        String methodName = method.getName();

        long duration = DURATION_STOPWATCH.get()
            .stop()
            .elapsed(TimeUnit.MILLISECONDS);

        IntegrationServer.reportTestCaseRun(method, duration, testResult.getStatus() == ITestResult.SUCCESS);

        log.debug("Available permits before releasing: {}", SEMAPHORE.availablePermits());
        SEMAPHORE.release(1);
        log.debug("Available permits after releasing {}: {}", methodName, SEMAPHORE.availablePermits());

        StatusLogger.incrementFinishedTestCount(method, duration);
        deleteTestUsers(methodName);

        EMAIL_DOMAIN.remove();
        DURATION_STOPWATCH.remove();
        if (!isNull(SERVER_PORT_CACHED_ITEM.get())) {
            ConnectionProvider.releaseServerPort(SERVER_PORT_CACHED_ITEM.get());
            SERVER_PORT_CACHED_ITEM.remove();
        }

        log.debug("Test {} completed", methodName);

        if (ITestResult.FAILURE == testResult.getStatus()) {
            saveStackTrace(testResult.getTestClass().getRealClass().getName(), testResult.getName(), testResult.getThrowable());
        }
    }

    @SneakyThrows
    private void saveStackTrace(String className, String method, Throwable throwable) {
        String directory = getReportDirectory(className, method);
        String fileName = directory + "/exception.json";
        log.debug("Exception fileName: {}", fileName);

        String exception = isNull(throwable) ? "null" : OBJECT_MAPPER_WRAPPER.writeValueAsPrettyString(ExceptionConverter.map(throwable));

        Path filePath = Paths.get(fileName);
        Path dirPath = Paths.get(directory);

        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Files.writeString(filePath, exception);
    }

    protected String getReportDirectory(String className, String method) {
        String[] split = className.split("\\.");
        String clazz = split[split.length - 1];

        return "error_report/" + TEST_START_TIME.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh_mm_ss")) + "/" + getTestType() + "/" + clazz + "_" + method + "_" + className.hashCode();
    }

    protected abstract String getTestType();

    private synchronized static void deleteTestUsers(String method) {
        log.debug("Deleting testUsers for method {}...", method);
        DatabaseUtil.setMarkedForDeletionByEmailLike(getEmailDomain());
    }
}
