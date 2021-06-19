package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class FriendDataControllerImplTestIt_getFriends {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String CHARACTER_NAME = "character-name";
    public static final UUID FRIENDSHIP_ID = UUID.randomUUID();

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private CharacterDao characterDao;

    @Autowired
    private FriendshipDao friendshipDao;

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
    public void getFriends() {
        SkyXploreCharacter character = SkyXploreCharacter.builder()
            .userId(USER_ID_2)
            .name(CHARACTER_NAME)
            .build();
        characterDao.save(character);

        Friendship friendship = Friendship.builder()
            .friendshipId(FRIENDSHIP_ID)
            .friend1(USER_ID_2)
            .friend2(USER_ID_1)
            .build();
        friendshipDao.save(friendship);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .get(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_GET_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        List<FriendshipResponse> friends = Arrays.stream(response.getBody().as(FriendshipResponse[].class))
            .collect(Collectors.toList());

        assertThat(friends).containsExactly(FriendshipResponse.builder().friendId(USER_ID_2).friendshipId(FRIENDSHIP_ID).friendName(CHARACTER_NAME).build());
    }
}