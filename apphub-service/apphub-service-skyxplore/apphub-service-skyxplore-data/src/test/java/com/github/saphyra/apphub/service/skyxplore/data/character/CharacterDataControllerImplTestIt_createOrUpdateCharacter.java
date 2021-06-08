package com.github.saphyra.apphub.service.skyxplore.data.character;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class CharacterDataControllerImplTestIt_createOrUpdateCharacter {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String CHARACTER_NAME = "character-name";
    private static final String NEW_CHARACTER_NAME = "new-character-name";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private CharacterDao characterDao;

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
    public void nullCharacterName() {
        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(model)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getParams()).containsEntry("name", "must not be null");
    }

    @Test
    public void characterNameTooShort() {
        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .name("as")
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(model)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_SHORT.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void characterNameTooLong() {
        SkyXploreCharacterModel model = SkyXploreCharacterModel.builder()
            .name(Stream.generate(() -> "a").limit(31).collect(Collectors.joining()))
            .build();

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(model)
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_TOO_LONG.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void characterNameAlreadyExists() {
        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER.toBuilder().userId(USER_ID_2).build()))
            .body(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CHARACTER_NAME_ALREADY_EXISTS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
    }

    @Test
    public void characterNameAlreadyExistsForTheSameUser() {
        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void createCharacter() {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(characterDao.findById(USER_ID_1)).contains(SkyXploreCharacter.builder().userId(USER_ID_1).name(CHARACTER_NAME).build());
    }

    @Test
    public void updateCharacterName() {
        RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(SkyXploreCharacterModel.builder().name(NEW_CHARACTER_NAME).build())
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_CREATE_OR_UPDATE_CHARACTER));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(characterDao.findById(USER_ID_1)).contains(SkyXploreCharacter.builder().userId(USER_ID_1).name(NEW_CHARACTER_NAME).build());
    }
}