package com.github.saphyra.apphub.integration.core;

import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ObjectMapperWrapper;
import com.github.saphyra.apphub.integration.framework.concurrent.ExecutorServiceBean;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

@Slf4j
@Listeners(SkipDisabledTestsInterceptor.class)
public class TestBase {
    public static final ExecutorServiceBean EXECUTOR_SERVICE = new ExecutorServiceBean(Executors.newCachedThreadPool());
    public static final ObjectMapperWrapper OBJECT_MAPPER_WRAPPER = new ObjectMapperWrapper();

    private static final int AVAILABLE_PERMITS = 10;
    private static final Semaphore SEMAPHORE = new Semaphore(AVAILABLE_PERMITS);

    public static int SERVER_PORT;
    public static int DATABASE_PORT;
    public static String DATABASE_NAME;
    public static boolean REST_LOGGING_ENABLED;
    public static List<String> DISABLED_TEST_GROUPS;
    public static Connection CONNECTION;

    private static int TOTAL_TEST_COUNT;
    private static final Set<String> FINISHED_TESTS = ConcurrentHashMap.newKeySet();
    private static final List<String> TEST_START_ORDER = new Vector<>();

    private static final ThreadLocal<String> EMAIL_DOMAIN = new ThreadLocal<>();

    public static String getEmailDomain() {
        return EMAIL_DOMAIN.get();
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite(ITestContext context) throws Exception {
        DISABLED_TEST_GROUPS = Arrays.asList(Optional.ofNullable(System.getProperty("disabledGroups"))
            .orElse("")
            .split(","));
        log.info("Disabled test groups: {}", DISABLED_TEST_GROUPS);

        TOTAL_TEST_COUNT = (int) context.getSuite()
            .getAllMethods()
            .stream()
            .filter(TestBase::isEnabled)
            .count();
        log.info("Total test count: {}", TOTAL_TEST_COUNT);

        SERVER_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("serverPort"), "serverPort is null"));
        log.info("ServerPort: {}", SERVER_PORT);

        DATABASE_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("databasePort"), "serverPort is null"));
        log.info("DatabasePort: {}", DATABASE_PORT);

        DATABASE_NAME = System.getProperty("databaseName", "postgres");
        log.info("DatabaseName: {}", DATABASE_NAME);

        REST_LOGGING_ENABLED = Optional.ofNullable(System.getProperty("restLoggingEnabled"))
            .map(Boolean::parseBoolean)
            .orElse(true);
        log.info("RestLoggingEnabled: {}", REST_LOGGING_ENABLED);

        for (ITestNGMethod method : context.getAllTestMethods()) {
            method.getXmlTest().getSuite().addListener(SkipDisabledTestsInterceptor.class.getName());
            if (Boolean.parseBoolean(System.getProperty("retryEnabled"))) {
                method.setRetryAnalyzerClass(RetryAnalyzerImpl.class);
            }
        }

        System.setProperty("testng.show.stack.frames", "true");

        CONNECTION = DatabaseUtil.getConnection(); //Checking if database is accessible
    }

    public static boolean isEnabled(ITestNGMethod method) {
        List<String> groups = Arrays.asList(method.getGroups());

        return TestBase.DISABLED_TEST_GROUPS.stream()
            .noneMatch(groups::contains);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() throws SQLException {
        WebDriverFactory.stopDrivers();

        if (nonNull(CONNECTION)) {
            CONNECTION.close();
        }

        for (int i = 0; i < TEST_START_ORDER.size(); i++) {
            log.info("{} - {}", i, TEST_START_ORDER.get(i));
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws InterruptedException {
        FINISHED_TESTS.remove(getMethodIdentifier(method));
        String testMethod = method.getDeclaringClass().getSimpleName() + "-" + method.getName();

        log.debug("Available permits before acquiring: {}", SEMAPHORE.availablePermits());
        Stopwatch stopwatch = Stopwatch.createStarted();
        acquirePermit(method, stopwatch);

        EMAIL_DOMAIN.set(testMethod.toLowerCase() + "-" + UUID.randomUUID().toString().split("-")[0]);
        TEST_START_ORDER.add(getTestCaseName(method));
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

        incrementFinishedTestCount(method);
        deleteTestUsers(methodName);

        EMAIL_DOMAIN.remove();

        log.debug("Test {} completed", methodName);
    }

    private synchronized void incrementFinishedTestCount(Method method) {
        String methodIdentifier = getMethodIdentifier(method);
        FINISHED_TESTS.add(methodIdentifier);

        int finishedPercentage = (int) Math.floor((double) FINISHED_TESTS.size() / TOTAL_TEST_COUNT * 100);

        StringBuilder progressBar = new StringBuilder();

        for (int i = 0; i < 100; i += 2) {
            if (i < finishedPercentage) {
                progressBar.append("=");
            } else {
                progressBar.append("_");
            }
        }

        log.info("{} {}% {}/{} - {}", progressBar, finishedPercentage, FINISHED_TESTS.size(), TOTAL_TEST_COUNT, getTestCaseName(method));
    }

    private static String getTestCaseName(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        return String.format("%s %s.%s", TestType.getLabel(declaringClass.getSuperclass()), declaringClass.getSimpleName(), method.getName());
    }

    private static String getMethodIdentifier(Method method) {
        return method.getDeclaringClass().getName() + method.getName();
    }

    private synchronized static void deleteTestUsers(String method) {
        log.debug("Deleting testUsers for method {}...", method);
        DatabaseUtil.setMarkedForDeletionByEmailLike(getEmailDomain());
    }

    @RequiredArgsConstructor
    private enum TestType {
        BE(BackEndTest.class, "[BE]"),
        FE(SeleniumTest.class, "[FE]");

        private final Class<? extends TestBase> clazz;
        private final String label;

        public static String getLabel(Class<?> declaringClass) {
            return Arrays.stream(values())
                .filter(testType -> testType.clazz.equals(declaringClass))
                .findFirst()
                .map(testType -> testType.label)
                .orElseThrow(() -> new RuntimeException("No TestType present for " + declaringClass.getName()));
        }
    }
}
