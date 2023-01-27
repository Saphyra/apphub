package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendRequestFactoryTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private FriendRequestFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(FRIEND_REQUEST_ID);

        FriendRequest result = underTest.create(SENDER_ID, FRIEND_ID);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.getFriendId()).isEqualTo(FRIEND_ID);
        assertThat(result.getSenderId()).isEqualTo(SENDER_ID);
    }
}