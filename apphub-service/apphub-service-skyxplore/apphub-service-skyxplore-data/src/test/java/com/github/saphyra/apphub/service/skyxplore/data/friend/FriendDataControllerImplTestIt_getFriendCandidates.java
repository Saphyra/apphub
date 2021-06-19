package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
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
public class FriendDataControllerImplTestIt_getFriendCandidates {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID_1)
        .roles(Arrays.asList("SKYXPLORE"))
        .build();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID_3 = UUID.randomUUID();
    private static final UUID USER_ID_4 = UUID.randomUUID();

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationApiClient localizationApiClient;

    @Autowired
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Autowired
    private CharacterDao characterDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

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
    public void getFriendCandidates() {
        SkyXploreCharacter ownCharacter = createCharacter(USER_ID_1, "own");
        SkyXploreCharacter friendRequestSent = createCharacter(USER_ID_2, "requested");
        SkyXploreCharacter alreadyFriend = createCharacter(USER_ID_3, "friend");
        SkyXploreCharacter friendCandidate = createCharacter(USER_ID_4, "candidate");
        characterDao.saveAll(Arrays.asList(ownCharacter, friendRequestSent, alreadyFriend, friendCandidate));

        FriendRequest friendRequest = FriendRequest.builder()
            .friendRequestId(UUID.randomUUID())
            .senderId(USER_ID_2)
            .friendId(USER_ID_1)
            .build();
        friendRequestDao.save(friendRequest);

        Friendship friendship = Friendship.builder()
            .friendshipId(UUID.randomUUID())
            .friend1(USER_ID_3)
            .friend2(USER_ID_1)
            .build();
        friendshipDao.save(friendship);

        Response response = RequestFactory.createAuthorizedRequest(accessTokenHeaderConverter.convertDomain(ACCESS_TOKEN_HEADER))
            .body(new OneParamRequest<>("r-N"))
            .post(UrlFactory.create(serverPort, Endpoints.SKYXPLORE_SEARCH_FOR_FRIENDS));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        List<SkyXploreCharacterModel> result = Arrays.stream(response.getBody().as(SkyXploreCharacterModel[].class))
            .collect(Collectors.toList());

        assertThat(result).containsExactly(SkyXploreCharacterModel.builder().id(USER_ID_4).name(CHARACTER_NAME + "-candidate").build());
    }

    private SkyXploreCharacter createCharacter(UUID userId, String nameSuffix) {
        return SkyXploreCharacter.builder()
            .userId(userId)
            .name(CHARACTER_NAME + "-" + nameSuffix)
            .build();
    }
}