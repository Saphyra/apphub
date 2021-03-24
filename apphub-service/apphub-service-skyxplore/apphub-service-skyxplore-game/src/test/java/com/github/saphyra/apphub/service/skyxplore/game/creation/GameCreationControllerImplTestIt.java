package com.github.saphyra.apphub.service.skyxplore.game.creation;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.skyxplore.game.SkyxploreGameApplication;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = {SkyxploreGameApplication.class})
public class GameCreationControllerImplTestIt {
    private static final String CHARACTER_NAME = "character-name";
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private GameDao gameDao;

    @MockBean
    private CharacterProxy characterProxy;

    @MockBean
    private MessageSenderProxy messageSenderProxy;

    @MockBean
    private SkyXploreDataGameClient skyXploreDataGameClient;

    @Before
    public void setUp() {
        given(characterProxy.getCharacterByUserId(any(UUID.class))).willReturn(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build());
    }

    @After
    public void clear() {
        gameDao.deleteAll();
    }

    @Test
    @Ignore
    public void loadTest() throws InterruptedException {
        int gameCount = 50;
        Stream.generate(() -> "")
            .limit(gameCount)
            .parallel()
            .forEach(s -> {
                Map<UUID, UUID> members = new HashMap<UUID, UUID>() {{
                    put(UUID.randomUUID(), null);
                }};

                SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
                    .host(UUID.randomUUID())
                    .members(members)
                    .alliances(new HashMap<>())
                    .settings(SkyXploreGameCreationSettingsRequest.builder()
                        .universeSize(UniverseSize.LARGE)
                        .systemAmount(SystemAmount.COMMON)
                        .systemSize(SystemSize.LARGE)
                        .planetSize(PlanetSize.LARGE)
                        .aiPresence(AiPresence.EVERYWHERE)
                        .build()
                    )
                    .build();

                Response response = RequestFactory.createRequest()
                    .body(request)
                    .put(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_CREATE_GAME));

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
            });

        for (int i = 0; i < 200; i++) {
            if (gameDao.size() == gameCount) {
                return;
            }

            Thread.sleep(1000);
        }

        fail("Game is not created.");
    }

    @Test
    public void largeGame() throws InterruptedException {
        UUID host = UUID.randomUUID();
        Map<UUID, UUID> members = CollectionUtils.toMap(
            new BiWrapper<>(host, ALLIANCE_ID),
            new BiWrapper<>(UUID.randomUUID(), ALLIANCE_ID),
            new BiWrapper<>(UUID.randomUUID(), ALLIANCE_ID),
            new BiWrapper<>(UUID.randomUUID(), ALLIANCE_ID),
            new BiWrapper<>(UUID.randomUUID(), ALLIANCE_ID),
            new BiWrapper<>(UUID.randomUUID(), ALLIANCE_ID),
            new BiWrapper<>(UUID.randomUUID(), ALLIANCE_ID),
            new BiWrapper<>(UUID.randomUUID(), ALLIANCE_ID)
        );

        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(host)
            .members(members)
            .alliances(CollectionUtils.singleValueMap(ALLIANCE_ID, ALLIANCE_NAME))
            .settings(SkyXploreGameCreationSettingsRequest.builder()
                .universeSize(UniverseSize.LARGE)
                .systemAmount(SystemAmount.COMMON)
                .systemSize(SystemSize.LARGE)
                .planetSize(PlanetSize.LARGE)
                .aiPresence(AiPresence.EVERYWHERE)
                .build()
            )
            .gameName(GAME_NAME)
            .build();

        Response response = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_CREATE_GAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        for (int i = 0; i < 20; i++) {
            if (gameDao.size() > 0) {
                return;
            }

            Thread.sleep(1000);
        }

        fail("Game is not created.");
    }

    @Test
    public void smallGame() throws InterruptedException {
        UUID playerId = UUID.randomUUID();
        UUID allianceId = UUID.randomUUID();
        Map<UUID, UUID> members = CollectionUtils.toMap(
            new BiWrapper<>(playerId, allianceId),
            new BiWrapper<>(UUID.randomUUID(), allianceId),
            new BiWrapper<>(UUID.randomUUID(), allianceId),
            new BiWrapper<>(UUID.randomUUID(), allianceId),
            new BiWrapper<>(UUID.randomUUID(), allianceId),
            new BiWrapper<>(UUID.randomUUID(), allianceId)
        );
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(playerId)
            .members(members)
            .alliances(CollectionUtils.singleValueMap(allianceId, ALLIANCE_NAME))
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

        Response response = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_CREATE_GAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        for (int i = 0; i < 20; i++) {
            if (gameDao.size() > 0) {
                return;
            }

            Thread.sleep(1000);
        }

        fail("Game is not created.");
    }
}