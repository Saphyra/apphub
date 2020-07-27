package com.github.saphyra.apphub.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.integration.common.framework.*;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.UserRoleResponse;
import com.github.saphyra.util.ObjectMapperWrapper;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.DataConstants.VALID_PASSWORD;
import static com.github.saphyra.apphub.integration.common.framework.DataConstants.VALID_PASSWORD2;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TestBase {
    public static final ObjectMapperWrapper OBJECT_MAPPER_WRAPPER = new ObjectMapperWrapper(new ObjectMapper());

    public static int SERVER_PORT;
    public static int DATABASE_PORT;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        SERVER_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("serverPort"), "serverPort is null"));
        log.info("ServerPort: {}", SERVER_PORT);

        DATABASE_PORT = Integer.parseInt(Objects.requireNonNull(System.getProperty("databasePort"), "serverPort is null"));
        log.info("DatabasePort: {}", DATABASE_PORT);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() throws SQLException {
        deleteTestUsers();

        if (!isNull(DatabaseUtil.CONNECTION)) {
            DatabaseUtil.CONNECTION.close();
        }
    }

    private void deleteTestUsers() {
        log.info("Deleting testUsers...");
        Language language = Language.HUNGARIAN;
        RegistrationParameters registrationParameters = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, registrationParameters.toRegistrationRequest());
        DatabaseUtil.addRoleByEmail(registrationParameters.getEmail(), "ADMIN");

        UUID accessTokenId = IndexPageActions.login(language, registrationParameters.getEmail(), registrationParameters.getPassword());
        deleteUsersWithPassword(language, registrationParameters, VALID_PASSWORD, accessTokenId);
        deleteUsersWithPassword(language, registrationParameters, VALID_PASSWORD2, accessTokenId);

        deleteUser(registrationParameters.getEmail(), registrationParameters.getPassword());
    }

    private void deleteUsersWithPassword(Language language, RegistrationParameters registrationParameters, String password, UUID accessTokenId) {
        UserRoleResponse[] userResponses = getCandidates(language, accessTokenId);
        log.info("Deleting {} number of test accounts", userResponses.length);

        Arrays.stream(userResponses)
            .filter(userRoleResponse -> !userRoleResponse.getEmail().equals(registrationParameters.getEmail()))
            .forEach(userRoleResponse -> deleteUser(userRoleResponse.getEmail(), password));
    }

    private UserRoleResponse[] getCandidates(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>("@test.com"))
            .post(UrlFactory.create(Endpoints.GET_ROLES));

        return response.getBody().as(UserRoleResponse[].class);
    }

    private void deleteUser(String email, String password) {
        try {
            log.info("Deleting user {}", email);
            LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
            Language language = Language.HUNGARIAN;
            UUID accessTokenId = IndexPageActions.login(language, loginRequest);

            Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
                .body(new OneParamRequest<>(password))
                .delete(UrlFactory.create(Endpoints.DELETE_ACCOUNT));

            assertThat(response.getStatusCode()).isEqualTo(200);
            log.info("User deleted: {}", email);
        } catch (Throwable e) {
            log.warn("Failed deleting user {}", email, e);
        }
    }
}
