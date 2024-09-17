package com.github.saphyra.apphub.service.user.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.web_content.client.LocalizationClient;
import com.github.saphyra.apphub.api.user.model.login.LoginRequest;
import com.github.saphyra.apphub.api.user.model.login.InternalAccessTokenResponse;
import com.github.saphyra.apphub.api.user.model.login.LoginResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.event.EmptyEvent;
import com.github.saphyra.apphub.lib.event.RefreshAccessTokenExpirationEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class AuthenticationControllerItTest {
    private static final UUID ACCESS_TOKEN_ID_1 = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID_2 = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID_3 = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID_4 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String LOCALE = "locale";
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String USERNAME = "username";
    private static final User USER = User.builder()
        .userId(USER_ID)
        .email(EMAIL)
        .username(USERNAME)
        .password(new PasswordService().hashPassword(PASSWORD, USER_ID))
        .language(LOCALE)
        .passwordFailureCount(0)
        .build();
    private static final String ROLE = "role";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private AccessTokenDao accessTokenDao;

    @Autowired
    private Base64Encoder base64Encoder;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @MockBean
    private LocalizationClient localizationApi;

    private final ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS));

    @AfterEach
    public void clear() {
        accessTokenDao.deleteAll();
        roleDao.deleteAll();
        userDao.deleteAll();
    }

    @Test
    public void deleteExpiredAccessTokens() {
        LocalDateTime referenceDate = LocalDateTime.now(ZoneOffset.UTC);

        AccessToken expiredNonPersistentAccessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID)
            .persistent(false)
            .lastAccess(referenceDate.minusDays(1).withNano(0))
            .build();
        AccessToken validNonPersistentAccessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_2)
            .userId(USER_ID)
            .persistent(false)
            .lastAccess(referenceDate.plusHours(1).withNano(0))
            .build();
        AccessToken expiredPersistentAccessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_3)
            .userId(USER_ID)
            .persistent(true)
            .lastAccess(referenceDate.minusYears(3).withNano(0))
            .build();
        AccessToken validPersistentAccessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_4)
            .userId(USER_ID)
            .persistent(true)
            .lastAccess(referenceDate.minusMonths(2).withNano(0))
            .build();
        accessTokenDao.saveAll(Arrays.asList(expiredNonPersistentAccessToken, validNonPersistentAccessToken, expiredPersistentAccessToken, validPersistentAccessToken));

        SendEventRequest<?> request = new SendEventRequest<>();
        request.setEventName(EmptyEvent.DELETE_EXPIRED_ACCESS_TOKENS_EVENT_NAME);

        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(request))
            .post(UrlFactory.create(serverPort, Endpoints.EVENT_DELETE_EXPIRED_ACCESS_TOKENS));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(accessTokenDao.findAll()).containsExactlyInAnyOrder(validNonPersistentAccessToken, validPersistentAccessToken);
    }

    @Test
    public void updateLastAccess() {
        LocalDateTime referenceDate = LocalDateTime.now(ZoneOffset.UTC);

        AccessToken accessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID)
            .persistent(false)
            .lastAccess(referenceDate.minusDays(1))
            .build();
        accessTokenDao.save(accessToken);

        SendEventRequest<RefreshAccessTokenExpirationEvent> request = new SendEventRequest<>();
        request.setEventName(RefreshAccessTokenExpirationEvent.EVENT_NAME);
        request.setPayload(new RefreshAccessTokenExpirationEvent(ACCESS_TOKEN_ID_1));

        Response response = RequestFactory.createRequest()
            .body(objectMapperWrapper.writeValueAsString(request))
            .post(UrlFactory.create(serverPort, Endpoints.EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(accessTokenDao.findById(ACCESS_TOKEN_ID_1.toString()).get().getLastAccess()).isAfter(referenceDate);
    }

    @Test
    public void login_badPassword() {
        userDao.save(USER);
        given(localizationApi.translate(ErrorCode.BAD_CREDENTIALS.name(), LOCALE)).willReturn(LOCALIZED_MESSAGE);

        LoginRequest loginRequest = LoginRequest.builder()
            .userIdentifier(EMAIL)
            .password("wrong-password")
            .build();
        Response response = getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ErrorResponse errorResponse = objectMapperWrapper.readValue(response.getBody().asString(), ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS);
        assertThat(errorResponse.getParams()).isEqualTo(new HashMap<>());
    }

    @Test
    public void login_rememberMe() {
        userDao.save(USER);
        given(localizationApi.translate(ErrorCode.BAD_CREDENTIALS.name(), LOCALE)).willReturn(LOCALIZED_MESSAGE);

        LoginRequest loginRequest = LoginRequest.builder()
            .userIdentifier(EMAIL)
            .password(PASSWORD)
            .rememberMe(true)
            .build();
        Response response = getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        LoginResponse loginResponse = objectMapperWrapper.readValue(response.getBody().asString(), LoginResponse.class);
        assertThat(loginResponse.getAccessTokenId()).isNotNull();
        assertThat(loginResponse.getExpirationDays()).isEqualTo(365);

        assertThat(accessTokenDao.findById(loginResponse.getAccessTokenId().toString())).isNotEmpty();
        assertThat(accessTokenDao.findById(loginResponse.getAccessTokenId().toString()).get().isPersistent()).isTrue();
        assertThat(accessTokenDao.findById(loginResponse.getAccessTokenId().toString()).get().getUserId()).isEqualTo(USER_ID);
    }

    @Test
    public void login_shortSession() {
        userDao.save(USER);
        given(localizationApi.translate(ErrorCode.BAD_CREDENTIALS.name(), LOCALE)).willReturn(LOCALIZED_MESSAGE);

        LoginRequest loginRequest = LoginRequest.builder()
            .userIdentifier(EMAIL)
            .password(PASSWORD)
            .rememberMe(false)
            .build();
        Response response = getLoginResponse(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        LoginResponse loginResponse = objectMapperWrapper.readValue(response.getBody().asString(), LoginResponse.class);
        assertThat(loginResponse.getAccessTokenId()).isNotNull();
        assertThat(loginResponse.getExpirationDays()).isNull();

        assertThat(accessTokenDao.findById(loginResponse.getAccessTokenId().toString())).isNotEmpty();
        assertThat(accessTokenDao.findById(loginResponse.getAccessTokenId().toString()).get().isPersistent()).isFalse();
        assertThat(accessTokenDao.findById(loginResponse.getAccessTokenId().toString()).get().getUserId()).isEqualTo(USER_ID);
    }

    private Response getLoginResponse(LoginRequest loginRequest) {
        return RequestFactory.createRequest()
            .header(Constants.LOCALE_HEADER, LOCALE)
            .body(objectMapperWrapper.writeValueAsString(loginRequest))
            .post(UrlFactory.create(serverPort, Endpoints.LOGIN));
    }

    @Test
    public void logout() {
        LocalDateTime referenceDate = LocalDateTime.now();

        AccessToken accessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID)
            .persistent(false)
            .lastAccess(referenceDate.plusHours(1))
            .build();
        accessTokenDao.save(accessToken);

        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .userId(USER_ID)
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .build();

        Response response = RequestFactory.createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, base64Encoder.encode(objectMapperWrapper.writeValueAsString(accessTokenHeader)))
            .post(UrlFactory.create(serverPort, Endpoints.LOGOUT));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(accessTokenDao.findById(ACCESS_TOKEN_ID_1.toString())).isEmpty();
    }

    @Test
    public void getAccessTokenById_accessTokenNotFound() {
        Response response = getAccessTokenByIdResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getAccessTokenById_accessTokenExpired() {
        LocalDateTime referenceDate = LocalDateTime.now();

        AccessToken accessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID)
            .persistent(false)
            .lastAccess(referenceDate.minusYears(2))
            .build();
        accessTokenDao.save(accessToken);

        Response response = getAccessTokenByIdResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getAccessTokenById() {
        LocalDateTime referenceDate = LocalDateTime.now(ZoneOffset.UTC);

        AccessToken accessToken = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID)
            .persistent(false)
            .lastAccess(referenceDate)
            .build();
        accessTokenDao.save(accessToken);

        Role role = Role.builder()
            .roleId(UUID.randomUUID())
            .userId(USER_ID)
            .role(ROLE)
            .build();
        roleDao.save(role);

        Response response = getAccessTokenByIdResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        InternalAccessTokenResponse internalAccessTokenResponse = objectMapperWrapper.readValue(response.getBody().asString(), InternalAccessTokenResponse.class);
        assertThat(internalAccessTokenResponse.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID_1);
        assertThat(internalAccessTokenResponse.getUserId()).isEqualTo(USER_ID);
        assertThat(internalAccessTokenResponse.getRoles()).containsExactly(ROLE);
    }

    private Response getAccessTokenByIdResponse() {
        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("accessTokenId", ACCESS_TOKEN_ID_1.toString());
        return RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID, uriParams));
    }
}