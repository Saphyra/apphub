package com.github.saphyra.apphub.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.integration.common.framework.CustomObjectMapper;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Listeners(SkipDisabledTestsInterceptor.class)
public class TestBase {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    public static final CustomObjectMapper OBJECT_MAPPER_WRAPPER = new CustomObjectMapper(new ObjectMapper());

    public static int SERVER_PORT;
    public static int DATABASE_PORT;
    public static boolean REST_LOGGING_ENABLED;
    public static List<String> DISABLED_TEST_GROUPS;

    private static final ThreadLocal<String> EMAIL_DOMAIN = new ThreadLocal<>();
    private static final ThreadLocal<SoftAssertions> SOFT_ASSERTIONS = new ThreadLocal<>();

    public static String getEmailDomain() {
        return EMAIL_DOMAIN.get();
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite(ITestContext context) {
        SERVER_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("serverPort"), "serverPort is null"));
        log.info("ServerPort: {}", SERVER_PORT);

        DATABASE_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("databasePort"), "serverPort is null"));
        log.info("DatabasePort: {}", DATABASE_PORT);

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

        DISABLED_TEST_GROUPS = Arrays.asList(Optional.ofNullable(System.getProperty("disabledGroups"))
            .orElse("")
            .split(","));

        System.setProperty("testng.show.stack.frames", "true");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) {
        String testMethod = method.getDeclaringClass().getSimpleName() + "-" + method.getName();
        EMAIL_DOMAIN.set(testMethod.toLowerCase() + "-" + UUID.randomUUID().toString().split("-")[0]);
        SOFT_ASSERTIONS.set(new SoftAssertions());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod() {
        deleteTestUsers();

        EMAIL_DOMAIN.remove();
    }

    @AfterMethod(alwaysRun = true)
    public void assertSoftAssertions() {
        SOFT_ASSERTIONS.get()
            .assertAll();
    }

    private static void deleteTestUsers() {
        log.debug("Deleting testUsers...");
        DatabaseUtil.setMarkedForDeletionByEmailLike(getEmailDomain());
    }

    public static SoftAssertions getSoftAssertions() {
        return SOFT_ASSERTIONS.get();
    }
}
