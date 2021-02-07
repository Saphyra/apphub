package com.github.saphyra.apphub.service.skyxplore.data.game_data;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.test.common.TestConstants;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class GameDataControllerImplTestIt_getGameData {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @Test
    public void gameDataNotFound() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GET_ITEM_DATA, "dataId", "asd"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.DATA_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void getGameData() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GET_ITEM_DATA, "dataId", "community_center"));

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.getBody().jsonPath().getString("id")).isEqualTo("community_center");
    }
}