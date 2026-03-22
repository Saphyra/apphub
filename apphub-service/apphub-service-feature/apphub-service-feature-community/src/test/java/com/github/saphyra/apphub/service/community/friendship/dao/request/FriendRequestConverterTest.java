package com.github.saphyra.apphub.service.community.friendship.dao.request;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendRequestConverterTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID RECEIVER_ID = UUID.randomUUID();
    private static final String FRIEND_REQUEST_ID_STRING = "friend-request-id";
    private static final String SENDER_ID_STRING = "sender-id";
    private static final String RECEIVER_ID_STRING = "receiver-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private FriendRequestConverter underTest;

    @Test
    public void convertDomain() {
        FriendRequest domain = FriendRequest.builder()
            .friendRequestId(FRIEND_REQUEST_ID)
            .senderId(SENDER_ID)
            .receiverId(RECEIVER_ID)
            .build();

        given(uuidConverter.convertDomain(FRIEND_REQUEST_ID)).willReturn(FRIEND_REQUEST_ID_STRING);
        given(uuidConverter.convertDomain(SENDER_ID)).willReturn(SENDER_ID_STRING);
        given(uuidConverter.convertDomain(RECEIVER_ID)).willReturn(RECEIVER_ID_STRING);

        FriendRequestEntity result = underTest.convertDomain(domain);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID_STRING);
        assertThat(result.getSenderId()).isEqualTo(SENDER_ID_STRING);
        assertThat(result.getReceiverId()).isEqualTo(RECEIVER_ID_STRING);
    }

    @Test
    public void convertEntity() {
        FriendRequestEntity domain = FriendRequestEntity.builder()
            .friendRequestId(FRIEND_REQUEST_ID_STRING)
            .senderId(SENDER_ID_STRING)
            .receiverId(RECEIVER_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(FRIEND_REQUEST_ID_STRING)).willReturn(FRIEND_REQUEST_ID);
        given(uuidConverter.convertEntity(SENDER_ID_STRING)).willReturn(SENDER_ID);
        given(uuidConverter.convertEntity(RECEIVER_ID_STRING)).willReturn(RECEIVER_ID);

        FriendRequest result = underTest.convertEntity(domain);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.getSenderId()).isEqualTo(SENDER_ID);
        assertThat(result.getReceiverId()).isEqualTo(RECEIVER_ID);
    }
}