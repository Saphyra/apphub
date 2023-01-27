package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class FriendRequestTest {
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    public static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();

    private final FriendRequest underTest = FriendRequest.builder()
        .friendRequestId(FRIEND_REQUEST_ID)
        .senderId(SENDER_ID)
        .friendId(FRIEND_ID)
        .build();

    @Test
    public void getOtherId_senderId() {
        assertThat(underTest.getOtherId(SENDER_ID)).isEqualTo(FRIEND_ID);
    }

    @Test
    public void getOtherId_friendId() {
        assertThat(underTest.getOtherId(FRIEND_ID)).isEqualTo(SENDER_ID);
    }

    @Test
    public void getOtherId_unknownId() {
        Throwable ex = catchThrowable(() -> underTest.getOtherId(UUID.randomUUID()));

        ExceptionValidator.validateInvalidParam(ex, "userId", "Not related to friendRequest " + FRIEND_REQUEST_ID);
    }
}