package com.github.saphyra.apphub.service.skyxplore.game.service.chat;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class SkyXploreGameChatControllerImplTestIt_getPlayers {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID_1 = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String USERNAME = "username";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private GameDao gameDao;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Before
    public void setUp() {
        given(localizationApiClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @After
    public void clear() {
        gameDao.deleteAll();
    }

    @Test
    public void getPlayers() {
        Player player1 = Player.builder()
            .ai(true)
            .build();
        Player player2 = Player.builder()
            .ai(false)
            .connected(false)
            .build();
        Player player3 = Player.builder()
            .ai(false)
            .connected(true)
            .userId(PLAYER_ID_1)
            .username(USERNAME)
            .build();

        Game game = Game.builder()
            .players(CollectionUtils.toMap(
                new BiWrapper<>(USER_ID_1, player1),
                new BiWrapper<>(UUID.randomUUID(), player2),
                new BiWrapper<>(UUID.randomUUID(), player3)
            ))
            .build();
        gameDao.put(game);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GAME_GET_PLAYERS));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        SkyXploreCharacterModel[] models = response.getBody().as(SkyXploreCharacterModel[].class);
        assertThat(models).hasSize(1);
        assertThat(models[0].getId()).isEqualTo(PLAYER_ID_1);
        assertThat(models[0].getName()).isEqualTo(USERNAME);
    }
}