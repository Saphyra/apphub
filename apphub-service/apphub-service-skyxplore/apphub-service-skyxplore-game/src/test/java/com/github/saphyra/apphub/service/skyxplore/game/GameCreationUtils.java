package com.github.saphyra.apphub.service.skyxplore.game;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GameCreationUtils {
    public static Game createGame(int serverPort, GameDao gameDao, UUID userId, String gameName) throws InterruptedException {
        Map<UUID, UUID> members = CollectionUtils.singleValueMap(userId, null);
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(userId)
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
            .gameName(gameName)
            .build();

        Response gameCreationResponse = RequestFactory.createRequest()
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_INTERNAL_CREATE_GAME));

        assertThat(gameCreationResponse.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        for (int i = 0; i < 20; i++) {
            Optional<Game> gameOptional = gameDao.findByUserId(userId);
            if (gameOptional.isPresent()) {
                return gameOptional.get();
            }

            Thread.sleep(1000);
        }

        throw new IllegalStateException("Game was not created");
    }
}
