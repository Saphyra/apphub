package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class FriendshipTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();

    private final Friendship underTest = Friendship.builder()
        .friendshipId(FRIENDSHIP_ID)
        .userId(USER_ID)
        .friendId(FRIEND_ID)
        .build();

    @Test
    public void getOtherUserId_userId() {
        assertThat(underTest.getOtherUserId(USER_ID)).isEqualTo(FRIEND_ID);
    }

    @Test
    public void getOtherUserId_friendId() {
        assertThat(underTest.getOtherUserId(FRIEND_ID)).isEqualTo(USER_ID);
    }

    @Test
    public void getOtherUserId_anotherId() {
        Throwable ex = catchThrowable(() -> underTest.getOtherUserId(FRIENDSHIP_ID));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }
}