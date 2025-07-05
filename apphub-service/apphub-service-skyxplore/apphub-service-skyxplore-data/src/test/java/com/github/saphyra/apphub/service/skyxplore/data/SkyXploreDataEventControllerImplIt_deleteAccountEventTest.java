package com.github.saphyra.apphub.service.skyxplore.data;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.web_content.client.LocalizationClient;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class SkyXploreDataEventControllerImplIt_deleteAccountEventTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALIZED_MESSAGE = "localized-message";
    private static final String CHARACTER_NAME = "character-name";

    @LocalServerPort
    private int serverPort;

    @MockBean
    private LocalizationClient localizationClient;

    @Autowired
    private CharacterDao characterDao;

    @Autowired
    private FriendshipDao friendshipDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private List<AbstractDao<?, ?, ?, ?>> daos;

    @BeforeEach
    public void setUp() {
        given(localizationClient.translate(anyString(), eq(TestConstants.DEFAULT_LOCALE))).willReturn(LOCALIZED_MESSAGE);
    }

    @AfterEach
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
            .post(UrlFactory.create(serverPort, GenericEndpoints.EVENT_DELETE_ACCOUNT));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(friendRequestDao.findAll()).isEmpty();
        assertThat(friendshipDao.findAll()).isEmpty();
        assertThat(characterDao.findAll()).isEmpty();
    }
}