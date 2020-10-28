package com.github.saphyra.apphub.service.user.controller;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessTokenDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.TestConstants;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class UserEventControllerImplTestIt {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final String EMAIL_1 = "email-1@asd.asd";
    private static final String EMAIL_2 = "email-2@asd.asd";
    private static final String USERNAME_1 = "username-1";
    private static final String USERNAME_2 = "username-2";
    private static final String PASSWORD = "password";
    private static final UUID ACCESS_TOKEN_ID_1 = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID_2 = UUID.randomUUID();

    @LocalServerPort
    private int serverPort;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccessTokenDao accessTokenDao;

    @After
    public void clear() {
        userDao.deleteAll();
        accessTokenDao.deleteAll();
    }

    @Test
    public void deleteAccountEvent() {
        User user1 = User.builder()
            .userId(USER_ID_1)
            .email(EMAIL_1)
            .username(USERNAME_1)
            .password(PASSWORD)
            .language(TestConstants.DEFAULT_LOCALE)
            .build();
        User user2 = User.builder()
            .userId(USER_ID_2)
            .email(EMAIL_2)
            .username(USERNAME_2)
            .password(PASSWORD)
            .language(TestConstants.DEFAULT_LOCALE)
            .build();
        userDao.saveAll(Arrays.asList(user1, user2));

        AccessToken accessToken1 = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_1)
            .userId(USER_ID_1)
            .lastAccess(LocalDateTime.now())
            .build();
        AccessToken accessToken2 = AccessToken.builder()
            .accessTokenId(ACCESS_TOKEN_ID_2)
            .userId(USER_ID_2)
            .lastAccess(LocalDateTime.now())
            .build();
        accessTokenDao.saveAll(Arrays.asList(accessToken1, accessToken2));

        SendEventRequest<DeleteAccountEvent> sendEventRequest = SendEventRequest.<DeleteAccountEvent>builder()
            .payload(new DeleteAccountEvent(USER_ID_1))
            .build();

        Response response = RequestFactory.createRequest()
            .body(sendEventRequest)
            .post(UrlFactory.create(serverPort, Endpoints.DELETE_ACCOUNT_EVENT));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(userDao.findAll()).containsExactly(user2);
        assertThat(accessTokenDao.findAll()).containsExactly(accessToken2);
    }
}