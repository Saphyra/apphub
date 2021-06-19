package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendRequestConverterTest {
    private static final String FRIEND_REQUEST_ID_STRING = "friend-request-id";
    private static final String FRIEND_ID_STRING = "friend-id";
    private static final String SENDER_ID_STRING = "sender-id";
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FriendRequestConverter underTest;

    @Test
    public void convertEntity() {
        FriendRequestEntity entity = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_STRING)
            .friendId(FRIEND_ID_STRING)
            .senderId(SENDER_ID_STRING)
            .build();
        given(uuidConverter.convertEntity(FRIEND_REQUEST_ID_STRING)).willReturn(FRIEND_REQUEST_ID);
        given(uuidConverter.convertEntity(FRIEND_ID_STRING)).willReturn(FRIEND_ID);
        given(uuidConverter.convertEntity(SENDER_ID_STRING)).willReturn(SENDER_ID);

        FriendRequest result = underTest.convertEntity(entity);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.getFriendId()).isEqualTo(FRIEND_ID);
        assertThat(result.getSenderId()).isEqualTo(SENDER_ID);
    }

    @Test
    public void convertDomain() {
        FriendRequest domain = FriendRequest.builder()
            .friendRequestId(FRIEND_REQUEST_ID)
            .friendId(FRIEND_ID)
            .senderId(SENDER_ID)
            .build();
        given(uuidConverter.convertDomain(FRIEND_REQUEST_ID)).willReturn(FRIEND_REQUEST_ID_STRING);
        given(uuidConverter.convertDomain(FRIEND_ID)).willReturn(FRIEND_ID_STRING);
        given(uuidConverter.convertDomain(SENDER_ID)).willReturn(SENDER_ID_STRING);

        FriendRequestEntity result = underTest.convertDomain(domain);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID_STRING);
        assertThat(result.getFriendId()).isEqualTo(FRIEND_ID_STRING);
        assertThat(result.getSenderId()).isEqualTo(SENDER_ID_STRING);
    }
}