package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.skyxplore.response.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendRequestQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final String CHARACTER_NAME = "character-name";
    private static final UUID SENDER_ID = UUID.randomUUID();

    @Mock
    private CharacterDao characterDao;

    @Mock
    private FriendRequestDao friendRequestDao;

    @InjectMocks
    private FriendRequestQueryService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private SkyXploreCharacter character;

    @Test
    public void getSentFriendRequests() {
        given(friendRequestDao.getBySenderId(USER_ID)).willReturn(Arrays.asList(friendRequest));
        given(friendRequest.getFriendRequestId()).willReturn(FRIEND_REQUEST_ID);
        given(friendRequest.getFriendId()).willReturn(FRIEND_ID);
        given(characterDao.findByIdValidated(FRIEND_ID)).willReturn(character);
        given(character.getName()).willReturn(CHARACTER_NAME);

        List<SentFriendRequestResponse> result = underTest.getSentFriendRequests(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.get(0).getFriendName()).isEqualTo(CHARACTER_NAME);
    }

    @Test
    public void getIncomingFriendRequests() {
        given(friendRequestDao.getByFriendId(USER_ID)).willReturn(Arrays.asList(friendRequest));
        given(friendRequest.getFriendRequestId()).willReturn(FRIEND_REQUEST_ID);
        given(friendRequest.getSenderId()).willReturn(SENDER_ID);
        given(characterDao.findByIdValidated(SENDER_ID)).willReturn(character);
        given(character.getName()).willReturn(CHARACTER_NAME);

        List<IncomingFriendRequestResponse> result = underTest.getIncomingFriendRequests(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.get(0).getSenderName()).isEqualTo(CHARACTER_NAME);
    }
}