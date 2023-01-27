package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendshipFactoryTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private FriendshipFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(FRIENDSHIP_ID);

        Friendship result = underTest.create(FRIEND_ID, SENDER_ID);

        assertThat(result.getFriendshipId()).isEqualTo(FRIENDSHIP_ID);
        assertThat(result.getFriend1()).isEqualTo(FRIEND_ID);
        assertThat(result.getFriend2()).isEqualTo(SENDER_ID);
    }
}