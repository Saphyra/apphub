package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.game.GameCreationUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class SkyXplorePlanetPopulationControllerImplTestIt_renameCitizen {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String GAME_NAME = "game-name";
    private static final String CHARACTER_NAME = "character-name";
    private static final String NEW_NAME = "new-name";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @MockBean
    private CharacterProxy characterProxy;

    @MockBean
    private MessageSenderApiClient messageSenderClient;

    @MockBean
    private SkyXploreSavedGameClient skyXploreSavedGameClient;

    @Autowired
    private GameDao gameDao;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
        given(characterProxy.getCharacterByUserId(any(UUID.class))).willReturn(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build());
    }

    @After
    public void clear() {
        gameDao.deleteAll();
    }

    @Test
    public void blankNewName() throws InterruptedException {
        Game game = GameCreationUtils.createGame(serverPort, gameDao, USER_ID_1, GAME_NAME);
        UUID planetId = getPlanetId(game);
        CitizenResponse citizen = getCitizen(planetId);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(new OneParamRequest<>(" "))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("citizenId", citizen.getCitizenId()))));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams()).containsEntry("value", "must not be null or blank");
    }

    @Test
    public void newNameTooLong() throws InterruptedException {
        Game game = GameCreationUtils.createGame(serverPort, gameDao, USER_ID_1, GAME_NAME);
        UUID planetId = getPlanetId(game);
        CitizenResponse citizen = getCitizen(planetId);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(new OneParamRequest<>(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("citizenId", citizen.getCitizenId()))));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LOCALIZED_MESSAGE);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM);
        assertThat(errorResponse.getParams()).containsEntry("value", "too long");
    }

    @Test
    public void citizenNotFound() throws InterruptedException {
        Game game = GameCreationUtils.createGame(serverPort, gameDao, USER_ID_1, GAME_NAME);
        UUID planetId = getPlanetId(game);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(new OneParamRequest<>(NEW_NAME))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("citizenId", UUID.randomUUID()))));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void renameCitizen() throws InterruptedException {
        Game game = GameCreationUtils.createGame(serverPort, gameDao, USER_ID_1, GAME_NAME);
        UUID planetId = getPlanetId(game);
        CitizenResponse citizen = getCitizen(planetId);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(new OneParamRequest<>(NEW_NAME))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("citizenId", citizen.getCitizenId()))));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        CitizenResponse modifiedCitizen = getCitizen(planetId, citizen.getCitizenId());
        assertThat(modifiedCitizen.getName()).isEqualTo(NEW_NAME);
    }

    private UUID getPlanetId(Game game) {
        return game.getUniverse()
            .getSystems()
            .values()
            .stream()
            .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
            .filter(p -> !isNull(p.getOwner()))
            .findFirst()
            .map(Planet::getPlanetId)
            .orElseThrow(() -> new RuntimeException("No populated planet"));
    }

    private CitizenResponse getCitizen(UUID planetId, UUID citizenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_PLANET_GET_POPULATION, "planetId", planetId));
        return Arrays.stream(response.getBody().as(CitizenResponse[].class))
            .filter(citizenResponse -> citizenResponse.getCitizenId().equals(citizenId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Citizen not found with id " + citizenId));
    }

    private CitizenResponse getCitizen(UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_PLANET_GET_POPULATION, "planetId", planetId));
        return Arrays.stream(response.getBody().as(CitizenResponse[].class))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Planet not populated"));
    }
}