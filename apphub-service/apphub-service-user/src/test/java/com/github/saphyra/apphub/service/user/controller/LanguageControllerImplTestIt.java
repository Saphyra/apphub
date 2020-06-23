package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.user.model.response.LanguageResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class LanguageControllerImplTestIt {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String EMAIL = "email@mail.hu";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String LOCALE = "locale";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

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
    public void changeLanguage_null(){
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .build();

        Response response = RequestFactory.createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(new OneParamRequest<>())
            .post(UrlFactory.create(serverPort, Endpoints.CHANGE_LANGUAGE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("value")).isEqualTo("language must not be null");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), "hu");
    }

    @Test
    public void changeLanguage_notSupported(){
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .build();

        Response response = RequestFactory.createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(new OneParamRequest<>("asd"))
            .post(UrlFactory.create(serverPort, Endpoints.CHANGE_LANGUAGE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("value")).isEqualTo("language not supported");

        verify(localizationApiClient).translate(ErrorCode.INVALID_PARAM.name(), "hu");
    }

    @Test
    public void changeLanguage(){
        User user = User.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LOCALE)
            .build();
        userDao.save(user);

        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .build();

        Response response = RequestFactory.createRequest()
            .header(Constants.ACCESS_TOKEN_HEADER, accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(new OneParamRequest<>("hu"))
            .post(UrlFactory.create(serverPort, Endpoints.CHANGE_LANGUAGE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(userDao.findById(USER_ID).getLanguage()).isEqualTo("hu");
    }

    @Test
    public void getLanguage_userNotFound() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", USER_ID);

        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.INTERNAL_GET_USER_LANGUAGE, paramMap));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);

        verify(localizationApiClient).translate(ErrorCode.USER_NOT_FOUND.name(), "hu");
    }

    @Test
    public void getLanguage() {
        User user = User.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LOCALE)
            .build();
        userDao.save(user);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", USER_ID);

        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.INTERNAL_GET_USER_LANGUAGE, paramMap));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getBody().asString()).isEqualTo(LOCALE);
    }

    @Test
    public void getLanguages(){
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(USER_ID)
            .build();

        User user = User.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(DEFAULT_LOCALE)
            .build();
        userDao.save(user);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .get(UrlFactory.create(serverPort, Endpoints.GET_LANGUAGES));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        List<LanguageResponse> responses = response.getBody()
            .jsonPath()
            .getList(".", LanguageResponse.class);

        assertThat(responses).contains(LanguageResponse.builder().language(DEFAULT_LOCALE).actual(true).build());
        assertThat(responses).contains(LanguageResponse.builder().language("en").actual(false).build());
    }
}
