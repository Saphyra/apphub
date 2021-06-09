package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.test.common.TestConstants.DEFAULT_LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class AccountControllerImplTestIt_UsernameChange {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final ChangeUsernameRequest REQUEST = ChangeUsernameRequest.builder()
        .username(USERNAME)
        .password(PASSWORD)
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .build();

    @LocalServerPort
    private int serverPort;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserDao userDao;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq("hu"))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        userDao.deleteAll();
    }

    @Test
    public void nullUsername() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(REQUEST.toBuilder().username(null).build())
            .post(UrlFactory.create(serverPort, Endpoints.ACCOUNT_CHANGE_USERNAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("username")).isEqualTo("must not be null");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), DEFAULT_LOCALE);
    }

    @Test
    public void usernameTooShort() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(REQUEST.toBuilder().username("as").build())
            .post(UrlFactory.create(serverPort, Endpoints.ACCOUNT_CHANGE_USERNAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_SHORT);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.USERNAME_TOO_SHORT.name(), DEFAULT_LOCALE);
    }

    @Test
    public void usernameTooLong() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(REQUEST.toBuilder().username(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())).build())
            .post(UrlFactory.create(serverPort, Endpoints.ACCOUNT_CHANGE_USERNAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_LONG);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.USERNAME_TOO_LONG.name(), DEFAULT_LOCALE);
    }

    @Test
    public void usernameAlreadyExists() {
        User user = User.builder()
            .userId(UUID.randomUUID())
            .username(USERNAME)
            .email("asd@asd.asd")
            .password("asdasd")
            .language(DEFAULT_LOCALE)
            .build();
        userDao.save(user);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(REQUEST.toBuilder().username(USERNAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.ACCOUNT_CHANGE_USERNAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.USERNAME_ALREADY_EXISTS.name(), DEFAULT_LOCALE);
    }

    @Test
    public void nullPassword() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(REQUEST.toBuilder().password(null).build())
            .post(UrlFactory.create(serverPort, Endpoints.ACCOUNT_CHANGE_USERNAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("password")).isEqualTo("must not be null");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), DEFAULT_LOCALE);
    }

    @Test
    public void invalidPassword() {
        User user = User.builder()
            .userId(USER_ID)
            .username("asda")
            .email("asd@asd.asda")
            .password(passwordService.hashPassword(PASSWORD + "a"))
            .language(DEFAULT_LOCALE)
            .build();
        userDao.save(user);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(REQUEST)
            .post(UrlFactory.create(serverPort, Endpoints.ACCOUNT_CHANGE_USERNAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INCORRECT_PASSWORD);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.INCORRECT_PASSWORD.name(), DEFAULT_LOCALE);
    }

    @Test
    public void changeUsername() {
        User user = User.builder()
            .userId(USER_ID)
            .username("asda")
            .email("asd@asd.asda")
            .password(passwordService.hashPassword(PASSWORD))
            .language(DEFAULT_LOCALE)
            .build();
        userDao.save(user);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(REQUEST)
            .post(UrlFactory.create(serverPort, Endpoints.ACCOUNT_CHANGE_USERNAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(userDao.findByIdValidated(USER_ID).getUsername()).isEqualTo(USERNAME);
    }
}