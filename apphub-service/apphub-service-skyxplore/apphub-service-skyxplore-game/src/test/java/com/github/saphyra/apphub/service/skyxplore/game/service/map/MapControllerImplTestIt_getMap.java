package com.github.saphyra.apphub.service.skyxplore.game.service.map;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
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
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class MapControllerImplTestIt_getMap {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String ROOM_TITLE = "room-title";
    private static final String GAME_NAME = "game-name";
    private static final String CHARACTER_NAME = "character-name";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @MockBean
    private CharacterProxy characterProxy;

    @MockBean
    private MessageSenderApiClient messageSenderClient;

    @MockBean
    private SkyXploreDataGameClient skyXploreDataGameClient;

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
    public void getMap() throws InterruptedException {
        Map<UUID, UUID> members = CollectionUtils.singleValueMap(USER_ID_1, null);
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(USER_ID_1)
            .members(members)
            .alliances(Collections.emptyMap())
            .settings(SkyXploreGameCreationSettingsRequest.builder()
                .universeSize(UniverseSize.SMALLEST)
                .systemAmount(SystemAmount.SMALL)
                .systemSize(SystemSize.SMALL)
                .planetSize(PlanetSize.SMALL)
                .aiPresence(AiPresence.NONE)
                .build()
            )
            .gameName(GAME_NAME)
            .build();

        Response gameCreationResponse = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_CREATE_GAME));

        assertThat(gameCreationResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        for (int i = 0; i < 20; i++) {
            if (gameDao.size() > 0) {
                break;
            }

            Thread.sleep(1000);
        }

        Game game = gameDao.findByUserIdValidated(USER_ID_1);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_MAP));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        MapResponse mapResponse = response.getBody().as(MapResponse.class);

        assertThat(mapResponse.getUniverseSize()).isEqualTo(game.getUniverse().getSize());
        assertThat(mapResponse.getSolarSystems()).hasSize(game.getUniverse().getSystems().size());
        assertThat(mapResponse.getConnections()).hasSize(game.getUniverse().getConnections().size());
    }
}