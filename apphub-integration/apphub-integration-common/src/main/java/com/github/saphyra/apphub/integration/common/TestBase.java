package com.github.saphyra.apphub.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.integration.common.framework.CustomObjectMapper;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.UserRoleResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.saphyra.apphub.integration.common.framework.DataConstants.VALID_PASSWORD;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.VALID_PASSWORD2;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestBase {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    public static final CustomObjectMapper OBJECT_MAPPER_WRAPPER = new CustomObjectMapper(new ObjectMapper());

    public static int SERVER_PORT;
    public static int DATABASE_PORT;
    public static boolean REST_LOGGING_ENABLED;

    private static final ThreadLocal<String> EMAIL_DOMAIN = new ThreadLocal<>();
    private static final ThreadLocal<SoftAssertions> SOFT_ASSERTIONS = new ThreadLocal<>();
    private static volatile RegistrationParameters superUser;
    private static volatile UUID accessTokenId;

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

        registerSuperuser();

        if (Boolean.parseBoolean(System.getProperty("retryEnabled"))) {
            for (ITestNGMethod method : context.getAllTestMethods()) {
                method.setRetryAnalyzerClass(RetryAnalyzerImpl.class);
            }
        }

        System.setProperty("testng.show.stack.frames", "true");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        deleteUser(superUser.getEmail(), superUser.getPassword());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        EMAIL_DOMAIN.set(UUID.randomUUID().toString());
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

    private static synchronized void deleteTestUsers() {
        log.debug("Deleting testUsers...");
        Language language = Language.HUNGARIAN;

        deleteUsersWithPassword(language, VALID_PASSWORD, accessTokenId);
        deleteUsersWithPassword(language, VALID_PASSWORD2, accessTokenId);
    }

    private void registerSuperuser() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, registrationParameters.toRegistrationRequest());
        DatabaseUtil.addRoleByEmail(registrationParameters.getEmail(), "ADMIN");
        superUser = registrationParameters;
        accessTokenId = IndexPageActions.login(language, superUser.getEmail(), superUser.getPassword());
        log.info("AccessTokenId of superUser: {}", accessTokenId);
    }

    private static void deleteUsersWithPassword(Language language, String password, UUID accessTokenId) {
        UserRoleResponse[] userResponses = getCandidates(language, accessTokenId);
        log.debug("Deleting {} number of test accounts", userResponses.length);

        Arrays.stream(userResponses)
            .forEach(userRoleResponse -> deleteUser(userRoleResponse.getEmail(), password));
    }

    private static UserRoleResponse[] getCandidates(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>("@" + getEmailDomain() + ".com"))
            .post(UrlFactory.create(Endpoints.USER_DATA_GET_USER_ROLES));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(UserRoleResponse[].class);
    }

    private static void deleteUser(String email, String password) {
        try {
            log.debug("Deleting user {}", email);
            LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
            Language language = Language.HUNGARIAN;
            UUID accessTokenId = IndexPageActions.login(language, loginRequest);

            Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
                .body(new OneParamRequest<>(password))
                .delete(UrlFactory.create(Endpoints.ACCOUNT_DELETE_ACCOUNT));

            assertThat(response.getStatusCode()).isEqualTo(200);
            log.debug("User deleted: {}", email);
        } catch (Throwable e) {
            log.error("Failed deleting user {}", email, e);
        }
    }

    public static SoftAssertions getSoftAssertions() {
        return SOFT_ASSERTIONS.get();
    }
}
