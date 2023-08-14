package com.github.saphyra.apphub.integration.core;

import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ObjectMapperWrapper;
import com.github.saphyra.apphub.integration.framework.concurrent.ExecutorServiceBean;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Listeners(SkipDisabledTestsInterceptor.class)
public class TestBase {
    public static final ExecutorServiceBean EXECUTOR_SERVICE = new ExecutorServiceBean(Executors.newCachedThreadPool());
    public static final ObjectMapperWrapper OBJECT_MAPPER_WRAPPER = new ObjectMapperWrapper();

    private static final Semaphore SEMAPHORE = new Semaphore(TestConfiguration.AVAILABLE_PERMITS);

    private static final ThreadLocal<String> EMAIL_DOMAIN = new ThreadLocal<>();

    public static String getEmailDomain() {
        return EMAIL_DOMAIN.get();
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite(ITestContext context) {
        log.info("Disabled test groups: {}", TestConfiguration.DISABLED_TEST_GROUPS);
        log.info("ServerPort: {}", TestConfiguration.SERVER_PORT);
        log.info("DatabasePort: {}", TestConfiguration.DATABASE_PORT);
        log.info("DatabaseName: {}", TestConfiguration.DATABASE_NAME);
        log.info("RestLoggingEnabled: {}", TestConfiguration.REST_LOGGING_ENABLED);

        StatusLogger.setTotalTestCount(context);

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
            .noneMatch(groups::contains);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() throws SQLException {
        WebDriverFactory.stopDrivers();

        TestConfiguration.CONNECTION.close();

        StatusLogger.logTestStartOrder();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws InterruptedException {
        TestGroupValidator.validateTestGroups(method);

        StatusLogger.removeFinishedTest(method);
        String testMethod = method.getDeclaringClass().getSimpleName() + "-" + method.getName();

        log.debug("Available permits before acquiring: {}", SEMAPHORE.availablePermits());
        Stopwatch stopwatch = Stopwatch.createStarted();
        acquirePermit(method, stopwatch);

        EMAIL_DOMAIN.set(testMethod.toLowerCase() + "-" + UUID.randomUUID().toString().split("-")[0]);
        StatusLogger.addToStartOrder(method);
    }

    private static synchronized void acquirePermit(Method method, Stopwatch stopwatch) throws InterruptedException {
        SEMAPHORE.acquire(1);
        stopwatch.stop();
        log.debug("Permit acquired for test {} in {}ms. Permits left: {}", method.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS), SEMAPHORE.availablePermits());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(Method method) {
        String methodName = method.getName();

        log.debug("Available permits before releasing: {}", SEMAPHORE.availablePermits());
        SEMAPHORE.release(1);
        log.debug("Available permits after releasing {}: {}", methodName, SEMAPHORE.availablePermits());

        StatusLogger.incrementFinishedTestCount(method);
        deleteTestUsers(methodName);

        EMAIL_DOMAIN.remove();

        log.debug("Test {} completed", methodName);
    }

    private synchronized static void deleteTestUsers(String method) {
        log.debug("Deleting testUsers for method {}...", method);
        DatabaseUtil.setMarkedForDeletionByEmailLike(getEmailDomain());
    }
}
