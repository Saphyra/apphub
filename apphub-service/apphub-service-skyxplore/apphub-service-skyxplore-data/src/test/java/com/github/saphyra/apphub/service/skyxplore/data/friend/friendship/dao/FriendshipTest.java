package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class FriendshipTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID_1 = UUID.randomUUID();
    private static final UUID FRIEND_ID_2 = UUID.randomUUID();

    private final Friendship underTest = Friendship.builder()
        .friendshipId(FRIENDSHIP_ID)
        .friend1(FRIEND_ID_1)
        .friend2(FRIEND_ID_2)
        .build();

    @Test
    public void getOtherId_friendId1() {
        assertThat(underTest.getOtherId(FRIEND_ID_1)).isEqualTo(FRIEND_ID_2);
    }

    @Test
    public void getOtherId_friendId2() {
        assertThat(underTest.getOtherId(FRIEND_ID_2)).isEqualTo(FRIEND_ID_1);
    }

    @Test
    public void getOtherId_unknownId() {
        Throwable ex = catchThrowable(() -> underTest.getOtherId(UUID.randomUUID()));

        ExceptionValidator.validateInvalidParam(ex, "userId", "Not related to friendship " + FRIENDSHIP_ID);
    }
}