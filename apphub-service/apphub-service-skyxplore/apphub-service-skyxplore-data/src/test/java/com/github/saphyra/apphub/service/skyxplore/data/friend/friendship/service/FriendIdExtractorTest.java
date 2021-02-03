package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FriendIdExtractorTest {
    private static final UUID FRIEND_1 = UUID.randomUUID();
    private static final UUID FRIEND_2 = UUID.randomUUID();
    private static final Friendship FRIENDSHIP = Friendship.builder()
        .friend1(FRIEND_1)
        .friend2(FRIEND_2)
        .build();

    @InjectMocks
    private FriendIdExtractor underTest;

    @Test
    public void id1() {
        UUID result = underTest.getFriendId(FRIENDSHIP, FRIEND_1);

        assertThat(result).isEqualTo(FRIEND_2);
    }

    @Test
    public void id2() {
        UUID result = underTest.getFriendId(FRIENDSHIP, FRIEND_2);

        assertThat(result).isEqualTo(FRIEND_1);
    }
}