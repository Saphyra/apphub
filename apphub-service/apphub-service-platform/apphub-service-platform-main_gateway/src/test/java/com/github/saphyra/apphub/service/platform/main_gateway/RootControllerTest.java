package com.github.saphyra.apphub.service.platform.main_gateway;

import com.github.saphyra.apphub.api.user.client.UserAuthenticationClient;
import com.github.saphyra.apphub.api.user.model.response.InternalAccessTokenResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RootControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();

    @LocalServerPort
    private int serverPort;

    @MockBean
    private UserAuthenticationClient userAuthenticationClient;

    @Test
    void getOwnUserId_nullAccessToken() {
        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.GET_OWN_USER_ID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void getOwnUserId() {
        given(userAuthenticationClient.getAccessTokenById(eq(ACCESS_TOKEN_ID), anyString())).willReturn(InternalAccessTokenResponse.builder().userId(USER_ID).build());

        Response response = RequestFactory.createRequest()
            .cookie(Constants.ACCESS_TOKEN_COOKIE, ACCESS_TOKEN_ID)
            .get(UrlFactory.create(serverPort, Endpoints.GET_OWN_USER_ID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(response.getBody().jsonPath().getUUID("value")).isEqualTo(USER_ID);
    }
}