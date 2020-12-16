package com.github.saphyra.apphub.service.skyxplore.game.creation;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.skyxplore.game.SkyxploreGameApplication;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = SkyxploreGameApplication.class)
public class GameCreationControllerImplTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private GameDao gameDao;

    @After
    public void clear() {
        gameDao.deleteAll();
    }

    @Test
    public void largeGame() throws InterruptedException {
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

        for (int i = 0; i < 200; i++) {
            if (gameDao.size() > 0) {
                return;
            }

            Thread.sleep(1000);
        }

        fail("Game is not created.");
    }

    @Test
    public void smallGame() throws InterruptedException {
        Map<UUID, UUID> members = new HashMap<UUID, UUID>() {{
            put(UUID.randomUUID(), null);
        }};

        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(UUID.randomUUID())
            .members(members)
            .alliances(new HashMap<>())
            .settings(SkyXploreGameCreationSettingsRequest.builder()
                .universeSize(UniverseSize.SMALLEST)
                .systemAmount(SystemAmount.SMALL)
                .systemSize(SystemSize.SMALL)
                .planetSize(PlanetSize.SMALL)
                .aiPresence(AiPresence.NONE)
                .build()
            )
            .build();

        Response response = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.INTERNAL_SKYXPLORE_CREATE_GAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        for (int i = 0; i < 200; i++) {
            if (gameDao.size() > 0) {
                return;
            }

            Thread.sleep(1000);
        }

        fail("Game is not created.");
    }
}