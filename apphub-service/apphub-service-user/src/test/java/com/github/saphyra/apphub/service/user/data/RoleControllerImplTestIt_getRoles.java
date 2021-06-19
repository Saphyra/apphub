package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.TestConstants;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class RoleControllerImplTestIt_getRoles {
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final String EMAIL_1 = "email-1";
    private static final String EMAIL_2 = "email-2";
    private static final String USERNAME_1 = "username-1";
    private static final String USERNAME_2 = "username-2";
    private static final String ROLE_1 = "role-1";
    private static final String ROLE_2 = "role-2";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        userDao.deleteAll();
        roleDao.deleteAll();
    }

    @Test
    public void getRoles_nullSearchText() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .roles(Arrays.asList(ADMIN_ROLE))
            .build();

        OneParamRequest<String> request = new OneParamRequest<>();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.USER_DATA_GET_USER_ROLES));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("searchText")).isEqualTo("must not be null");
    }

    @Test
    public void getRoles_tooShortSearchText() {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .roles(Arrays.asList(ADMIN_ROLE))
            .build();

        OneParamRequest<String> request = new OneParamRequest<>("as");

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.USER_DATA_GET_USER_ROLES));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams().get("searchText")).isEqualTo("too short");
    }

    @Test
    public void getRoles() {
        User user1 = User.builder()
            .userId(USER_ID_1)
            .email(EMAIL_1)
            .username(USERNAME_1)
            .password("")
            .language("")
            .build();
        userDao.save(user1);
        Role role1 = Role.builder()
            .roleId(UUID.randomUUID())
            .userId(USER_ID_1)
            .role(ROLE_1)
            .build();
        roleDao.save(role1);

        User user2 = User.builder()
            .userId(USER_ID_2)
            .email(EMAIL_2)
            .username(USERNAME_2)
            .password("")
            .language("")
            .build();
        userDao.save(user2);
        Role role2 = Role.builder()
            .roleId(UUID.randomUUID())
            .userId(USER_ID_2)
            .role(ROLE_2)
            .build();
        roleDao.save(role2);

        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .accessTokenId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .roles(Arrays.asList(ADMIN_ROLE))
            .build();

        OneParamRequest<String> request = new OneParamRequest<>("E-1");

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(accessTokenHeader))
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.USER_DATA_GET_USER_ROLES));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        List<UserRoleResponse> roles = Arrays.asList(response.getBody().as(UserRoleResponse[].class));
        assertThat(roles).hasSize(1);
        UserRoleResponse role = roles.get(0);
        assertThat(role.getUserId()).isEqualTo(USER_ID_1);
        assertThat(role.getUsername()).isEqualTo(USERNAME_1);
        assertThat(role.getEmail()).isEqualTo(EMAIL_1);
        assertThat(role.getRoles()).containsExactly(ROLE_1);
    }
}