package com.github.saphyra.apphub.service.skyxplore.data.character;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
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
public class CharacterDataControllerImplTestIt_internalGetCharacterByUserId {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String CHARACTER_NAME = "character-name";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private List<AbstractDao<?, ?, ?, ?>> daos;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        daos.forEach(AbstractDao::deleteAll);
    }

    @Test
    public void characterDoesNotExist() {
        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_GET_CHARACTER_BY_USER_ID, "userId", USER_ID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getCharacter() {
        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_GET_CHARACTER_BY_USER_ID, "userId", USER_ID));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        SkyXploreCharacterModel result = response.getBody().as(SkyXploreCharacterModel.class);
        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo(CHARACTER_NAME);
    }
}