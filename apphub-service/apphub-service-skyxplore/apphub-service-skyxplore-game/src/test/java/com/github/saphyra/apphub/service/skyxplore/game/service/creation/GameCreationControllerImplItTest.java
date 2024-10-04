package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.service.skyxplore.game.SkyXploreGameApplication;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.CharacterProxy;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = {SkyXploreGameApplication.class})
public class GameCreationControllerImplItTest {
    private static final String CHARACTER_NAME = "character-name";
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final UUID AI_USER_ID = UUID.randomUUID();
    private static final String AI_NAME = "ai-name";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private GameDao gameDao;

    @MockBean
    private CharacterProxy characterProxy;

    @MockBean
    private SkyXploreLobbyApiClient lobbyApiClient;

    @MockBean
    private SkyXploreSavedGameClient skyXploreSavedGameClient;

    @BeforeEach
    public void setUp() {
        given(characterProxy.getCharacterByUserId(any(UUID.class))).willReturn(SkyXploreCharacterModel.builder().name(CHARACTER_NAME).build());
    }

    @AfterEach
    public void clear() {
        gameDao.deleteAll();
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
            .players(members)
            .alliances(CollectionUtils.singleValueMap(ALLIANCE_ID, ALLIANCE_NAME))
            .settings(SkyXploreGameSettings.builder()
                .maxPlayersPerSolarSystem(5)
                .additionalSolarSystems(new Range<>(20, 30))
                .planetsPerSolarSystem(new Range<>(3, 5))
                .planetSize(new Range<>(10, 15))
                .build()
            )
            .ais(List.of(
                AiPlayer.builder()
                    .userId(AI_USER_ID)
                    .allianceId(null)
                    .name(AI_NAME)
                    .build()
            ))
            .gameName(GAME_NAME)
            .build();

        Response response = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_CREATE_GAME));

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
            .players(members)
            .alliances(CollectionUtils.singleValueMap(allianceId, ALLIANCE_NAME))
            .settings(SkyXploreGameSettings.builder()
                .maxPlayersPerSolarSystem(1)
                .additionalSolarSystems(new Range<>(2, 3))
                .planetsPerSolarSystem(new Range<>(0, 1))
                .planetSize(new Range<>(5, 6))
                .build()
            )
            .ais(List.of(
                AiPlayer.builder()
                    .userId(AI_USER_ID)
                    .allianceId(null)
                    .name(AI_NAME)
                    .build()
            ))
            .gameName(GAME_NAME)
            .build();

        Response response = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_INTERNAL_CREATE_GAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        for (int i = 0; i < 5; i++) {
            if (gameDao.size() > 0) {
                return;
            }

            Thread.sleep(1000);
        }

        fail("Game is not created.");
    }
}