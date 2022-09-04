package com.github.saphyra.apphub.service.skyxplore.data.friend.converter;

import com.github.saphyra.apphub.api.skyxplore.response.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendRequestToResponseConverterTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";

    @Mock
    private CharacterDao characterDao;

    @InjectMocks
    private FriendRequestToResponseConverter underTest;

    @Mock
    private SkyXploreCharacter character;

    @Before
    public void setUp() {
        given(character.getName()).willReturn(CHARACTER_NAME);
    }

    @Test
    public void toSentFriendRequest() {
        FriendRequest friendRequest = FriendRequest.builder()
            .friendRequestId(FRIEND_REQUEST_ID)
            .friendId(FRIEND_ID)
            .senderId(SENDER_ID)
            .build();

        given(characterDao.findByIdValidated(FRIEND_ID)).willReturn(character);

        SentFriendRequestResponse result = underTest.toSentFriendRequest(friendRequest);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.getFriendName()).isEqualTo(CHARACTER_NAME);
    }

    @Test
    public void toIncomingFriendRequest() {
        FriendRequest friendRequest = FriendRequest.builder()
            .friendRequestId(FRIEND_REQUEST_ID)
            .friendId(FRIEND_ID)
            .senderId(SENDER_ID)
            .build();

        given(characterDao.findByIdValidated(SENDER_ID)).willReturn(character);

        IncomingFriendRequestResponse result = underTest.toIncomingFriendRequest(friendRequest);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.getSenderName()).isEqualTo(CHARACTER_NAME);
    }
}