package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import com.github.saphyra.util.ObjectMapperWrapper;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class AccountControllerImplTestIt {
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "email@mail.hu";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String LOCALE = "locale";
    private static final RegistrationRequest REGISTRATION_REQUEST = RegistrationRequest.builder()
        .username(USERNAME)
        .password(PASSWORD)
        .email(EMAIL)
        .build();

    @LocalServerPort
    private int serverPort;

    @Autowired
    private ObjectMapperWrapper objectMapperWrapper;

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
    public void register_emailNull() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().email(null).build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("email")).isEqualTo("must not be null");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), "hu");
    }

    @Test
    public void register_emailInvalid() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().email("asd").build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("email")).isEqualTo("invalid format");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), "hu");
    }

    @Test
    public void register_emailAlreadyExists() {
        User user = User.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LOCALE)
            .build();
        userDao.save(user);

        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.EMAIL_ALREADY_EXISTS.name(), "hu");
    }

    @Test
    public void register_usernameNull() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().username(null).build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("username")).isEqualTo("must not be null");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), "hu");
    }

    @Test
    public void register_usernameTooShort() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().username("aa").build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.USERNAME_TOO_SHORT.name(), "hu");
    }

    @Test
    public void register_usernameTooLong() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().username(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())).build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.USERNAME_TOO_LONG.name(), "hu");
    }

    @Test
    public void register_usernameAlreadyExists() {
        User user = User.builder()
            .userId(USER_ID)
            .email(EMAIL + "a")
            .username(USERNAME)
            .password(PASSWORD)
            .language(LOCALE)
            .build();
        userDao.save(user);

        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.USERNAME_ALREADY_EXISTS.name(), "hu");
    }

    @Test
    public void register_nullPassword() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().password(null).build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("password")).isEqualTo("must not be null");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), "hu");
    }

    @Test
    public void register_passwordTooShort() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().password("as").build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.PASSWORD_TOO_SHORT.name(), "hu");
    }

    @Test
    public void register_passwordTooLong() {
        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(REGISTRATION_REQUEST.toBuilder().password(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())).build()))
            .post(UrlFactory.create(serverPort, Endpoints.REGISTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.PASSWORD_TOO_LONG.name(), "hu");
    }
}