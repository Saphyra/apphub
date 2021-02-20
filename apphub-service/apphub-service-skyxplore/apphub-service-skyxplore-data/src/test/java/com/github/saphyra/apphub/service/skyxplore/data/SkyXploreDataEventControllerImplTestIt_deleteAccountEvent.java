package com.github.saphyra.apphub.service.skyxplore.data;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.localization.client.LocalizationApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class SkyXploreDataEventControllerImplTestIt_deleteAccountEvent {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final AccessTokenHeader ACCESS_TOKEN_HEADER = AccessTokenHeader.builder()
        .accessTokenId(UUID.randomUUID())
        .userId(USER_ID)
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
    private FriendshipDao friendshipDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

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
    public void deleteAccountEvent() {
        FriendRequest friendRequest1 = FriendRequest.builder()
            .friendRequestId(UUID.randomUUID())
            .friendId(USER_ID)
            .senderId(UUID.randomUUID())
            .build();
        FriendRequest friendRequest2 = FriendRequest.builder()
            .friendRequestId(UUID.randomUUID())
            .friendId(UUID.randomUUID())
            .senderId(USER_ID)
            .build();
        friendRequestDao.saveAll(Arrays.asList(friendRequest1, friendRequest2));

        Friendship friendship1 = Friendship.builder()
            .friendshipId(UUID.randomUUID())
            .friend1(USER_ID)
            .friend2(UUID.randomUUID())
            .build();
        Friendship friendship2 = Friendship.builder()
            .friendshipId(UUID.randomUUID())
            .friend1(UUID.randomUUID())
            .friend2(USER_ID)
            .build();
        friendshipDao.saveAll(Arrays.asList(friendship1, friendship2));

        SkyXploreCharacter character = SkyXploreCharacter.builder()
            .userId(USER_ID)
            .name(CHARACTER_NAME)
            .build();
        characterDao.save(character);

        SendEventRequest<DeleteAccountEvent> request = SendEventRequest.<DeleteAccountEvent>builder()
            .eventName(DeleteAccountEvent.EVENT_NAME)
            .payload(new DeleteAccountEvent(USER_ID))
            .build();

        Response response = RequestFactory.createRequest()
            .body(request)
            .post(UrlFactory.create(serverPort, Endpoints.DELETE_ACCOUNT_EVENT));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(friendRequestDao.findAll()).isEmpty();
        assertThat(friendshipDao.findAll()).isEmpty();
        assertThat(characterDao.findAll()).isEmpty();
    }
}