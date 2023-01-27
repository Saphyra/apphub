package com.github.saphyra.apphub.service.community.friendship.dao.request;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class FriendRequestTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID RECEIVER_ID = UUID.randomUUID();

    private final FriendRequest underTest = FriendRequest.builder()
        .friendRequestId(FRIEND_REQUEST_ID)
        .senderId(SENDER_ID)
        .receiverId(RECEIVER_ID)
        .build();

    @Test
    public void getOtherUserId_senderId() {
        assertThat(underTest.getOtherUserId(SENDER_ID)).isEqualTo(RECEIVER_ID);
    }

    @Test
    public void getOtherUserId_receiverId() {
        assertThat(underTest.getOtherUserId(RECEIVER_ID)).isEqualTo(SENDER_ID);
    }

    @Test
    public void getOtherUserId_anotherId() {
        Throwable ex = catchThrowable(() -> underTest.getOtherUserId(FRIEND_REQUEST_ID));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }
}